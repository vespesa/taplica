(defproject com.github.vespesa/taplica "0.0.1-SNAPSHOT"
  :description "REPL friendly convenience library for tap> usage."
  :url "https://github.com/vespesa/taplica"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :repl-options {:init-ns taplica.core})
