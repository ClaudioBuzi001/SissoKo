#!/usr/bin/env node

/**
 * Automatic documentation generator for the Pizzeria project.
 *
 * Generates:
 *  - Root README.md with project overview and doc index
 *  - Backend class documentation with Mermaid class diagrams
 *  - Frontend entity documentation with Mermaid diagrams
 *  - Frontend feature overviews with relationship diagrams
 *
 * Supports a `--watch` flag to regenerate docs when source files change.
 */

const fs = require('fs');
const path = require('path');

const rootDir = path.resolve(__dirname, '..');
const backendSrcDir = path.join(
  rootDir,
  'backend',
  'pizzeria-service',
  'src',
  'main',
  'java'
);
const frontendSrcDir = path.join(
  rootDir,
  'frontend',
  'pizzeria-app',
  'src',
  'app'
);
const docsDir = path.join(rootDir, 'docs');
const backendDocsDir = path.join(docsDir, 'backend', 'classes');
const frontendEntitiesDir = path.join(docsDir, 'frontend', 'entities');
const frontendFeaturesDir = path.join(docsDir, 'frontend', 'features');
const readmePath = path.join(rootDir, 'README.md');

const watchMode = process.argv.slice(2).includes('--watch');

async function main() {
  const metadata = await collectMetadata();
  await ensureDir(backendDocsDir);
  await ensureDir(frontendEntitiesDir);
  await ensureDir(frontendFeaturesDir);

  await Promise.all([
    generateBackendClassDocs(metadata.backendClasses),
    generateFrontendEntityDocs(metadata.frontendEntities),
    generateFrontendFeatureDocs(metadata.features),
    generateReadme(metadata),
  ]);
}

async function collectMetadata() {
  const backendFiles = await listFiles(backendSrcDir, (file) =>
    file.endsWith('.java')
  );
  const frontendFiles = await listFiles(frontendSrcDir, (file) =>
    file.endsWith('.ts')
  );

  const backendClassesRaw = await Promise.all(
    backendFiles.map((file) => parseJavaFile(file))
  );
  const frontendEntitiesRaw = await Promise.all(
    frontendFiles.map((file) => parseTsFile(file))
  );

  const backendClasses = backendClassesRaw.filter(Boolean);
  const frontendEntities = frontendEntitiesRaw.flat().filter(Boolean);

  const features = await collectFeatures(frontendEntities);

  return {
    backendClasses,
    frontendEntities,
    features,
  };
}

async function collectFeatures(frontendEntities) {
  const featureMap = new Map();
  for (const entity of frontendEntities) {
    if (!entity.feature) {
      continue;
    }
    const entry = featureMap.get(entity.feature) || {
      name: entity.feature,
      path: entity.featurePath,
      entities: [],
    };
    entry.entities.push(entity);
    featureMap.set(entity.feature, entry);
  }
  return Array.from(featureMap.values()).sort((a, b) =>
    a.name.localeCompare(b.name)
  );
}

async function generateBackendClassDocs(classes) {
  const expectedFiles = new Set();
  const sorted = [...classes].sort((a, b) => a.name.localeCompare(b.name));

  for (const cls of sorted) {
    const filename = `${cls.name}.md`;
    expectedFiles.add(filename);
    const filePath = path.join(backendDocsDir, filename);
    const content = renderBackendClassDoc(cls);
    await fs.promises.writeFile(filePath, content, 'utf8');
  }

  await cleanupExtraFiles(backendDocsDir, expectedFiles);
}

async function generateFrontendEntityDocs(entities) {
  const expectedFiles = new Set();
  const sorted = [...entities].sort((a, b) => a.name.localeCompare(b.name));

  for (const entity of sorted) {
    const filename = `${entity.name}.md`;
    expectedFiles.add(filename);
    const filePath = path.join(frontendEntitiesDir, filename);
    const content = renderFrontendEntityDoc(entity);
    await fs.promises.writeFile(filePath, content, 'utf8');
  }

  await cleanupExtraFiles(frontendEntitiesDir, expectedFiles);
}

async function generateFrontendFeatureDocs(features) {
  const expectedFiles = new Set();
  for (const feature of features) {
    const filename = `${slugify(feature.name)}.md`;
    expectedFiles.add(filename);
    const filePath = path.join(frontendFeaturesDir, filename);
    const content = renderFeatureDoc(feature);
    await fs.promises.writeFile(filePath, content, 'utf8');
  }

  await cleanupExtraFiles(frontendFeaturesDir, expectedFiles);
}

async function generateReadme(metadata) {
  const { backendClasses, frontendEntities, features } = metadata;

  const backendLinks = backendClasses
    .sort((a, b) => a.name.localeCompare(b.name))
    .map(
      (cls) =>
        `- [${cls.name}](docs/backend/classes/${encodeURI(cls.name)}.md)`
    )
    .join('\n');

  const frontendLinks = frontendEntities
    .sort((a, b) => a.name.localeCompare(b.name))
    .map(
      (entity) =>
        `- [${entity.name}](docs/frontend/entities/${encodeURI(
          entity.name
        )}.md)`
    )
    .join('\n');

  const featureLinks = features
    .map(
      (feature) =>
        `- [${feature.name}](docs/frontend/features/${encodeURI(
          slugify(feature.name)
        )}.md)`
    )
    .join('\n');

  const readme = `# Pizzeria Platform

Applicazione full-stack per la gestione di pizzerie composta da:
- **Backend** Spring Boot (Java 21, MongoDB) che espone API RESTful e dati seed automatici.
- **Frontend** Angular 18 standalone con ricerca client-side delle pizzerie.
- **Infra** Docker Compose per MongoDB in locale e proxy dev Angular per integrare le API.

## Avvio rapido (locale)
- Backend: \`cd backend/pizzeria-service && mvn spring-boot:run\`
- Frontend: \`cd frontend/pizzeria-app && npm install && npm run start\`
- Database: \`docker compose up -d mongo\` (eseguito da \`mega_prova\`)

Le API sono raggiungibili su \`http://localhost:8080/api/pizzerias\`, mentre il frontend utilizza il proxy su \`http://localhost:4200\`.

## Esecuzione containerizzata
Richiede Docker 24+ e Docker Compose:

\`\`\`bash
docker compose build
docker compose up -d
\`\`\`

Servizi esposti:
- Frontend (Nginx): \`http://localhost:8081\`
- API Spring Boot: \`http://localhost:8080/api/pizzerias\`
- MongoDB: \`mongodb://localhost:27017/pizzeria\`

Per fermare tutto: \`docker compose down\` (aggiungi \`-v\` per rimuovere i dati Mongo).

## Documentazione
La documentazione è generata automaticamente via \`node scripts/generate-docs.js\`.

### Backend (classi)
${backendLinks || '- Nessuna classe rilevata.'}

### Frontend (entità)
${frontendLinks || '- Nessuna entità rilevata.'}

### Frontend (feature)
${featureLinks || '- Nessuna feature rilevata.'}

## Aggiornamento automatico
- Generazione manuale: \`node scripts/generate-docs.js\`
- Watcher continuo: \`node scripts/generate-docs.js --watch\`

In modalità watch, il generatore osserva backend e frontend e rigenera i file quando vengono creati o modificati i sorgenti. I file in \`docs/\` sono idempotenti e non vanno modificati a mano: personalizza il contenuto aggiornando i sorgenti oppure il generatore.

`;

  await fs.promises.writeFile(readmePath, readme, 'utf8');
}

function renderBackendClassDoc(cls) {
  const relPath = posixRelative(rootDir, cls.path);
  const fieldLines =
    cls.fields.length > 0
      ? cls.fields
          .map((field) => {
            const symbol = visibilitySymbol(field.visibility);
            return `    ${cls.name} : ${symbol}${field.name} : ${field.type}`;
          })
          .join('\n')
      : '';

  const methodLines =
    cls.methods.length > 0
      ? cls.methods
          .map((method) => {
            const symbol = visibilitySymbol(method.visibility);
            const params =
              method.params.length > 0
                ? method.params
                    .map((param) => `${param.type} ${param.name}`)
                    .join(', ')
                : '';
            return `    ${cls.name} : ${symbol}${method.name}(${params}) : ${method.returnType}`;
          })
          .join('\n')
      : '';

  const dependencyLines =
    cls.dependencies.length > 0
      ? cls.dependencies
          .map((dep) => `    ${cls.name} --> ${dep}`)
          .join('\n')
      : '';

  const annotations =
    cls.annotations.length > 0
      ? cls.annotations.map((ann) => `- \`${ann}\``).join('\n')
      : '- Nessuna annotazione.';

  const endpoints =
    cls.endpoints.length > 0
      ? cls.endpoints
          .map(
            (ep) =>
              `- \`${ep.method.toUpperCase()} ${ep.path}\` → \`${ep.handler}()\``
          )
          .join('\n')
      : null;

  const mermaidLines = [fieldLines, methodLines, dependencyLines]
    .filter((section) => section && section.trim().length > 0)
    .join('\n');

  const mermaidBlock =
    mermaidLines.length > 0
      ? `\`\`\`mermaid
classDiagram
    class ${cls.name}
${mermaidLines}
\`\`\`
`
      : '```mermaid\nclassDiagram\n    class ' + cls.name + '\n```\n';

  return `# ${cls.name}

- Tipo: \`${cls.type}\`
- Package: \`${cls.package}\`
- Percorso sorgente: \`${relPath}\`
- Annotazioni: 
${annotations}

## Diagramma
${mermaidBlock}

## Metodi
${
  cls.methods.length > 0
    ? cls.methods
        .map((method) => {
          const params =
            method.params.length > 0
              ? method.params
                  .map((param) => `\`${param.type} ${param.name}\``)
                  .join(', ')
              : 'nessun parametro';
          return `- \`${visibilitySymbol(method.visibility)} ${method.name}(${params}) : ${method.returnType}\``;
        })
        .join('\n')
    : '- Nessun metodo pubblico rilevato.'
}

${
  endpoints
    ? `## Endpoint REST\n${endpoints}\n`
    : ''
}
---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
`;
}

function renderFrontendEntityDoc(entity) {
  const relPath = posixRelative(rootDir, entity.path);
  const fieldLines =
    entity.fields.length > 0
      ? entity.fields
          .map((field) => {
            const symbol = visibilitySymbol(field.visibility);
            return `    ${entity.name} : ${symbol}${field.name} : ${field.type}`;
          })
          .join('\n')
      : '';

  const methodLines =
    entity.methods.length > 0
      ? entity.methods
          .map((method) => {
            const symbol = visibilitySymbol(method.visibility);
            const params =
              method.params.length > 0
                ? method.params
                    .map((param) => `${param.type} ${param.name}`)
                    .join(', ')
                : '';
            return `    ${entity.name} : ${symbol}${method.name}(${params}) : ${method.returnType}`;
          })
          .join('\n')
      : '';

  const dependencyLines =
    entity.dependencies.length > 0
      ? entity.dependencies
          .map((dep) => `    ${entity.name} --> ${dep}`)
          .join('\n')
      : '';

  const mermaidLines = [fieldLines, methodLines, dependencyLines]
    .filter((section) => section && section.trim().length > 0)
    .join('\n');

  const mermaidBlock =
    mermaidLines.length > 0
      ? `\`\`\`mermaid
classDiagram
    class ${entity.name}
${mermaidLines}
\`\`\`
`
      : '```mermaid\nclassDiagram\n    class ' + entity.name + '\n```\n';

  const decorators =
    entity.decorators.length > 0
      ? entity.decorators.map((dec) => `- \`${dec}\``).join('\n')
      : '- Nessun decorator rilevato.';

  return `# ${entity.name}

- Tipo: \`${entity.type}\`
- Percorso sorgente: \`${relPath}\`
- Feature: \`${entity.feature || 'global'}\`
- Decorator: 
${decorators}

## Diagramma
${mermaidBlock}

## Metodi
${
  entity.methods.length > 0
    ? entity.methods
        .map((method) => {
          const params =
            method.params.length > 0
              ? method.params
                  .map((param) => `\`${param.type} ${param.name}\``)
                  .join(', ')
              : 'nessun parametro';
          return `- \`${visibilitySymbol(method.visibility)} ${method.name}(${params}) : ${method.returnType}\``;
        })
        .join('\n')
    : '- Nessun metodo pubblico rilevato.'
}

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
`;
}

function renderFeatureDoc(feature) {
  const relPath = posixRelative(rootDir, feature.path);
  const entityLinks =
    feature.entities.length > 0
      ? feature.entities
          .sort((a, b) => a.name.localeCompare(b.name))
          .map(
            (entity) =>
              `- [${entity.name}](../entities/${encodeURI(entity.name)}.md)`
          )
          .join('\n')
      : '- Nessuna entità registrata.';

  const edges = [];
  for (const entity of feature.entities) {
    for (const dep of entity.dependencies) {
      if (feature.entities.find((e) => e.name === dep)) {
        edges.push(`    ${entity.name} --> ${dep}`);
      }
    }
  }

  const mermaidBlock =
    edges.length > 0
      ? `\`\`\`mermaid
graph TD
${Array.from(new Set(edges)).join('\n')}
\`\`\`
`
      : '```mermaid\ngraph TD\n    A[Feature vuota]\n```\n';

  return `# Feature \`${feature.name}\`

- Percorso sorgente: \`${relPath}\`
- Entità incluse:
${entityLinks}

## Relazioni interne
${mermaidBlock}

---
_Documento generato automaticamente. Modifica i file della feature o aggiorna lo script per personalizzare il contenuto._
`;
}

async function parseJavaFile(filePath) {
  const source = await fs.promises.readFile(filePath, 'utf8');
  const classRegex =
    /(?:public\s+)?(class|interface|enum)\s+([A-Z][A-Za-z0-9_]*)/;
  const classMatch = source.match(classRegex);
  if (!classMatch) {
    return null;
  }

  const packageMatch = source.match(/package\s+([a-zA-Z0-9_.]+);/);
  const pkg = packageMatch ? packageMatch[1] : '';
  const type = classMatch[1];
  const name = classMatch[2];

  const annotations = extractLeadingAnnotations(source, classMatch.index);
  const fields = extractJavaFields(source);
  const methods = extractJavaMethods(source, name);
  const dependencies = extractJavaDependencies(source, name);
  const endpoints = extractSpringEndpoints(source);

  return {
    name,
    type,
    package: pkg,
    path: filePath,
    annotations,
    fields,
    methods,
    dependencies,
    endpoints,
  };
}

function extractLeadingAnnotations(source, classIndex) {
  const snippet = source.slice(0, classIndex);
  const lines = snippet.split(/\r?\n/).map((line) => line.trim());
  const annotations = [];

  for (let i = lines.length - 1; i >= 0; i--) {
    const line = lines[i];
    if (!line) {
      continue;
    }
    if (line.startsWith('@')) {
      annotations.unshift(line.replace(/\(.*/, ''));
    } else if (
      line.startsWith('import ') ||
      line.startsWith('package ') ||
      line.startsWith('//')
    ) {
      break;
    } else {
      break;
    }
  }
  return Array.from(new Set(annotations));
}

function extractJavaFields(source) {
  const regex =
    /(public|protected|private)\s+(?:static\s+)?(?:final\s+)?([\w<>\[\].?]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)/g;
  const fields = [];
  let match;
  while ((match = regex.exec(source))) {
    fields.push({
      visibility: match[1],
      type: match[2].replace(/\s+/g, ' ').trim(),
      name: match[3],
    });
  }
  return fields;
}

function extractJavaMethods(source, className) {
  const regex =
    /(public|protected|private)\s+(?:static\s+)?([\w<>\[\].?]+)\s+([a-zA-Z0-9_]+)\s*\(([^)]*)\)\s*(?:throws [^{]+)?\s*\{/g;
  const methods = [];
  let match;
  while ((match = regex.exec(source))) {
    const visibility = match[1];
    const returnType = match[2].replace(/\s+/g, ' ').trim();
    const name = match[3];
    if (name === className) {
      continue; // constructor
    }
    const rawParams = match[4]
      .split(',')
      .map((param) => param.trim())
      .filter(Boolean);
    const params = rawParams.map((param, index) => {
      const parts = param.split(/\s+/);
      if (parts.length >= 2) {
        const paramName = parts.pop();
        const paramType = parts.join(' ');
        return { type: paramType, name: paramName };
      }
      return { type: param || `arg${index + 1}`, name: `arg${index + 1}` };
    });
    methods.push({
      visibility,
      name,
      returnType,
      params,
    });
  }
  return methods;
}

function extractJavaDependencies(source, className) {
  const regex = /import\s+([a-zA-Z0-9_.]+);/g;
  const deps = new Set();
  let match;
  while ((match = regex.exec(source))) {
    const fqcn = match[1];
    if (
      fqcn.startsWith('com.pizzeria') &&
      !fqcn.endsWith(className) &&
      !fqcn.endsWith('.config.PizzeriaServiceApplication')
    ) {
      deps.add(fqcn.split('.').pop());
    }
  }
  return Array.from(deps).sort();
}

function extractSpringEndpoints(source) {
  const regex =
    /@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)(\(([^)]*)\))?[\s\S]*?(public|protected|private)\s+[^\s]+\s+([a-zA-Z0-9_]+)\s*\(/g;
  const endpoints = [];
  let match;
  while ((match = regex.exec(source))) {
    const method = match[1];
    const httpMethod = method.replace('Mapping', '').toUpperCase();
    let path = '/';
    if (match[3]) {
      const argument = match[3].split(',')[0].trim();
      if (argument.startsWith('"') || argument.startsWith("'")) {
        path = argument.slice(1, -1);
      } else if (argument.includes('=')) {
        const explicit = argument.split('=').pop().trim();
        path =
          explicit.startsWith('"') || explicit.startsWith("'")
            ? explicit.slice(1, -1)
            : path;
      }
    }
    const handler = match[5];
    endpoints.push({
      method: httpMethod,
      path,
      handler,
    });
  }
  return endpoints;
}

async function parseTsFile(filePath) {
  const source = await fs.promises.readFile(filePath, 'utf8');
  if (filePath.endsWith('.spec.ts')) {
    return [];
  }
  const entities = [];
  const exportRegex =
    /@?[^\n]*export\s+(?:abstract\s+)?(class|interface)\s+([A-Z][A-Za-z0-9_]*)/g;
  let match;
  while ((match = exportRegex.exec(source))) {
    const type = match[1];
    const name = match[2];
    const block = extractBlock(source, match.index);
    const decorators = extractTsDecorators(source, match.index);
    const fields = extractTsFields(block);
    const methods = extractTsMethods(block, name);
    const dependencies = extractTsDependencies(source, name);

    const featureInfo = deriveFeatureInfo(filePath);

    entities.push({
      name,
      type,
      path: filePath,
      decorators,
      fields,
      methods,
      dependencies,
      feature: featureInfo.feature,
      featurePath: featureInfo.path,
    });
  }
  return entities;
}

function extractBlock(source, startIdx) {
  const braceIdx = source.indexOf('{', startIdx);
  if (braceIdx === -1) {
    return '';
  }
  let depth = 0;
  for (let i = braceIdx; i < source.length; i++) {
    const char = source[i];
    if (char === '{') {
      depth++;
    } else if (char === '}') {
      depth--;
      if (depth === 0) {
        return source.slice(braceIdx + 1, i);
      }
    }
  }
  return source.slice(braceIdx + 1);
}

function extractTsDecorators(source, classIndex) {
  const snippet = source.slice(0, classIndex);
  const decoratorRegex = /@([A-Za-z0-9_]+)/g;
  const decorators = new Set();
  let match;
  while ((match = decoratorRegex.exec(snippet))) {
    const name = match[1];
    if (/^[A-Z]/.test(name)) {
      decorators.add(`@${name}`);
    }
  }
  return Array.from(decorators);
}

function extractTsFields(block) {
  const fields = [];
  const disallowed = new Set([
    'this',
    'return',
    'const',
    'let',
    'var',
    'if',
    'for',
    'while',
    'else',
    'switch',
  ]);
  const regex =
    /^\s*(public|protected|private)?\s*(readonly\s+)?([a-zA-Z0-9_]+)\??\s*(?::\s*([^=;{]+))?(?:=\s*([^;{]+))?;/gm;
  let match;
  while ((match = regex.exec(block))) {
    const visibility = match[1] || 'public';
    const name = match[3];
    if (disallowed.has(name)) {
      continue;
    }
    const typeCandidate = match[4]
      ? match[4].trim()
      : match[5]
      ? match[5].trim()
      : 'unknown';
    const type = typeCandidate;
    fields.push({
      visibility,
      name,
      type,
    });
  }
  return fields;
}

function extractTsMethods(block, className) {
  const regex =
    /(public|protected|private)?\s*(?:async\s+)?([a-zA-Z0-9_]+)\s*\(([^)]*)\)\s*:\s*([^ {;]+)/g;
  const methods = [];
  let match;
  while ((match = regex.exec(block))) {
    const visibility = match[1] || 'public';
    const name = match[2];
    if (name === 'constructor' || name === className) {
      continue;
    }
    const params = match[3]
      .split(',')
      .map((param) => param.trim())
      .filter(Boolean)
      .map((param, index) => {
        const parts = param.split(':').map((part) => part.trim());
        if (parts.length === 2) {
          return { name: parts[0], type: parts[1] };
        }
        return { name: `arg${index + 1}`, type: parts[0] || 'unknown' };
      });
    const returnType = match[4];
    methods.push({
      visibility,
      name,
      params,
      returnType,
    });
  }
  return methods;
}

function extractTsDependencies(source, entityName) {
  const regex = /import\s+{([^}]+)}\s+from\s+['"]([^'"]+)['"]/g;
  const deps = new Set();
  let match;
  while ((match = regex.exec(source))) {
    const moduleSpecifier = match[2].trim();
    if (!moduleSpecifier.startsWith('.')) {
      continue;
    }
    const names = match[1]
      .split(',')
      .map((name) => name.trim())
      .filter((name) => name && name !== entityName);
    for (const name of names) {
      if (/^[A-Z]/.test(name)) {
        deps.add(name);
      }
    }
  }
  return Array.from(deps).sort();
}

function deriveFeatureInfo(filePath) {
  const directory = path.dirname(filePath);
  const parts = directory.split(path.sep);
  const appIndex = parts.findIndex((segment) => segment === 'app');
  if (appIndex === -1 || appIndex + 1 >= parts.length) {
    return { feature: null, path: directory };
  }
  const maybeFeature = parts[appIndex + 1];
  if (maybeFeature.includes('.')) {
    return { feature: null, path: directory };
  }
  const rawPath = parts.slice(0, appIndex + 2);
  const featurePath = path.isAbsolute(filePath)
    ? path.join(path.sep, ...rawPath.filter(Boolean))
    : path.join(...rawPath);
  return {
    feature: maybeFeature,
    path: featurePath,
  };
}

async function listFiles(dir, predicate) {
  const result = [];
  async function walk(current) {
    let entries;
    try {
      entries = await fs.promises.readdir(current, { withFileTypes: true });
    } catch {
      return;
    }
    for (const entry of entries) {
      const entryPath = path.join(current, entry.name);
      if (entry.isDirectory()) {
        await walk(entryPath);
      } else if (!predicate || predicate(entryPath)) {
        result.push(entryPath);
      }
    }
  }
  await walk(dir);
  return result;
}

async function ensureDir(dirPath) {
  await fs.promises.mkdir(dirPath, { recursive: true });
}

async function cleanupExtraFiles(dirPath, expected) {
  const entries = await fs.promises.readdir(dirPath, { withFileTypes: true });
  for (const entry of entries) {
    if (!entry.isFile()) {
      continue;
    }
    if (!expected.has(entry.name)) {
      await fs.promises.unlink(path.join(dirPath, entry.name));
    }
  }
}

function visibilitySymbol(visibility) {
  switch (visibility) {
    case 'public':
      return '+';
    case 'protected':
      return '#';
    case 'private':
      return '-';
    default:
      return '~';
  }
}

function posixRelative(from, to) {
  return path.relative(from, to).split(path.sep).join('/');
}

function slugify(text) {
  return text.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

function watchAndGenerate() {
  console.log('Avvio watcher documentazione...');
  const targets = [backendSrcDir, frontendSrcDir];
  const watchers = new Map();

  const runGenerator = debounce(async () => {
    try {
      await main();
      console.log('Documentazione aggiornata');
    } catch (err) {
      console.error('Errore durante la generazione della documentazione:', err);
    }
  }, 200);

  const watchDir = async (dir) => {
    if (watchers.has(dir)) {
      return;
    }
    let watcher;
    try {
      watcher = fs.watch(dir, async (_eventType, filename) => {
        if (!filename) {
          runGenerator();
          return;
        }
        const fullPath = path.join(dir, filename.toString());
        try {
          const stats = await fs.promises.stat(fullPath);
          if (stats.isDirectory()) {
            await watchDir(fullPath);
          }
        } catch {
          // File removed, ignore.
        }
        runGenerator();
      });
    } catch (err) {
      console.error(`Impossibile osservare ${dir}:`, err.message);
      return;
    }
    watchers.set(dir, watcher);

    let entries = [];
    try {
      entries = await fs.promises.readdir(dir, { withFileTypes: true });
    } catch {
      return;
    }
    for (const entry of entries) {
      if (entry.isDirectory()) {
        await watchDir(path.join(dir, entry.name));
      }
    }
  };

  const stop = () => {
    for (const watcher of watchers.values()) {
      watcher.close();
    }
    watchers.clear();
  };

  process.on('SIGINT', () => {
    stop();
    process.exit(0);
  });
  process.on('SIGTERM', () => {
    stop();
    process.exit(0);
  });

  Promise.all(targets.map((dir) => watchDir(dir)))
    .then(() => runGenerator())
    .catch((err) =>
      console.error('Errore di inizializzazione watcher:', err.message)
    );
}

function debounce(fn, wait) {
  let timer = null;
  return (...args) => {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => fn(...args), wait);
  };
}

if (watchMode) {
  watchAndGenerate();
} else {
  main().catch((err) => {
    console.error('Errore durante la generazione della documentazione:', err);
    process.exitCode = 1;
  });
}
