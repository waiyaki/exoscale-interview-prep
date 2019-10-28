(ns jobs.core
  (:require
   [clojure.string :as str]
   [clojure.tools.cli :as cli]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as timbre]
   [jobs.routes :as routes])
  (:gen-class))


(def cli-options
  [["-p" "--port PORT" "The port to start the server at."
    :id :port
    :default 8080
    :parse-fn #(Integer. %)
    :validate [int? "Must be an integer."]]])


(defn stop-app []
  (timbre/info "Stopping application...")
  (shutdown-agents)
  (timbre/info "Application stopped!"))


(defn start-app [args {:keys [handler] :as opts}]
  (let [{:keys [options errors]} (cli/parse-opts args cli-options)
        port                     (:port options)]
    (when errors
      (timbre/error (str/join "/n" errors))
      (System/exit 1))
    (jetty/run-jetty handler {:port  port :join? false})
    (timbre/info (str "Application started on http://localhost:" port))
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))))


(defn -main [& args]
  (start-app args {:handler routes/app}))
