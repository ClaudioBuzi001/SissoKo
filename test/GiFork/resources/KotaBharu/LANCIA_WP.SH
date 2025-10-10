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
xterm -geom 230x60+100+0 -T SRP -bg green -e "/usr/java/jdk-11.0.2/bin/java -Xmx4096M   \
   -Djava.system.class.loader=com.leonardo.infrastructure.plugins.framework.DynamicClassLoader \
   -Dlogfile.suffix=SRP \
   --illegal-access=deny \
   --module-path $PATH_TO_FX \
   --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.swing \
   --add-exports javafx.graphics/com.sun.javafx.print=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.javafx.perf=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.prism.j2d.print=ALL-UNNAMED \
   -classpath JeoViewer.jar:./libs \
   com.fourflight.WP.ECI.JeoViewer.application.JeoViewer resources_path=resourcesCustom --is_arp=true --name=SRP $*" &
sleep 4
xterm -geom 230x60+150+0 -T TRP -bg orange -e "/usr/java/jdk-11.0.2/bin/java -Xmx4096M   \
   -Djava.system.class.loader=com.leonardo.infrastructure.plugins.framework.DynamicClassLoader \
   -Dlogfile.suffix=TRP \
   --illegal-access=deny \
   --module-path $PATH_TO_FX \
   --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.swing \
   --add-exports javafx.graphics/com.sun.javafx.print=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.javafx.perf=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.prism.j2d.print=ALL-UNNAMED \
   -classpath JeoViewer.jar:./libs \
   com.fourflight.WP.ECI.JeoViewer.application.JeoViewer resources_path=resourcesCustom --is_arp=true --name=TRP $*" &
sleep 4
xterm -geom 230x60+200+0 -T FRP -bg green -e "/usr/java/jdk-11.0.2/bin/java -Xmx4096M   \
   -Djava.system.class.loader=com.leonardo.infrastructure.plugins.framework.DynamicClassLoader \
   -Dlogfile.suffix=FRP \
   --illegal-access=deny \
   --module-path $PATH_TO_FX \
   --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.swing \
   --add-exports javafx.graphics/com.sun.javafx.print=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.javafx.perf=ALL-UNNAMED \
   --add-exports javafx.graphics/com.sun.prism.j2d.print=ALL-UNNAMED \
   -classpath JeoViewer.jar:./libs \
   com.fourflight.WP.ECI.JeoViewer.application.JeoViewer resources_path=resourcesCustom --is_arp=true --name=FRP $*" &


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
dos2unix checkProcess.sh   
xterm -geom 230x60+300+0 -T MONITOR -bg blue -e "/usr/java/jdk-11.0.2/bin/java -classpath WPProcessMonitor.jar: application.WPProcessMonitor" &
