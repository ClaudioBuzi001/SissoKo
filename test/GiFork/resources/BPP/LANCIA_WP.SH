cd JeoViewer 
xterm -geom 230x60+0+0 -T JEOVIEWER -bg yellow -e "/usr/java/jdk-11.0.2/bin/java -Xms12G -Xmx12G \
   -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9000 \
   -Djava.system.class.loader=com.leonardo.infrastructure.plugins.framework.DynamicClassLoader \
   -Dlogfile.suffix \
   --illegal-access=deny \
   --module-path $PATH_TO_FX \
   --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.swing \
   --add-exports javafx.graphics/com.sun.javafx.print=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.javafx.perf=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.prism.j2d.print=ALL-UNNAMED \
   -classpath JeoViewer.jar:./libs \
   com.fourflight.WP.ECI.JeoViewer.application.JeoViewer resources_path=resourcesCustom $*"  &
sleep 4
cd ../GI-FORK
xterm -geom 230x60+300+0 -T GI-FORK -bg red -e "/usr/java/jdk-11.0.2/bin/java -Xmx4096M \
   -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8000 \
   -Dlog4j.configurationFile=file:./logger.xml \
   -Djava.system.class.loader=com.leonardo.infrastructure.plugins.framework.DynamicClassLoader \
   --illegal-access=deny \
   --module-path $PATH_TO_FX \
   --add-modules javafx.controls,javafx.fxml,javafx.graphics \
   -classpath GI-FORK.jar:./libs \
   applicationLIS.GiForkMain_LIS --prj=resources $*" &
