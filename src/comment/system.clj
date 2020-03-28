(ns comment.system
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [comment.handler :as handler]))

(def system-config
  {:comment/jetty      {:handler (ig/ref :comment/handler)
                        :port 3000}
   :comment/handler    {:db (ig/ref :comment/postgresql)}
   :comment/postgresql nil})

(defmethod ig/init-key :comment/jetty [_ {:keys [handler port]}]
  (println "Initialize jetty running on port" port)
  (jetty/run-jetty handler {:port port :join? false})
  handler)

(defmethod ig/init-key :comment/handler [_ {:keys [db]}]
  (println "Initialize handler")
  (handler/create-app db))

(defmethod ig/init-key :comment/postgresql [_ _]
  (println "Initialize postgresql")
  nil)

(defmethod ig/halt-key! :comment/jetty [_ jetty]
  (println "Halt jetty")
  (.stop jetty))

(defmethod ig/halt-key! :comment/handler [_ handler]
  (println "Halt handler"))

(defmethod ig/halt-key! :comment/postgresql [_ postgresql]
  (println "Halt postgresql")
  nil)

(defn -main []
  (ig/init system-config))

(comment
  (def system (ig/init system-config))
  (ig/halt! system))


