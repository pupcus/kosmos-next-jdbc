(ns kosmos.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.connection :as connection]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component])
  (:import [com.zaxxer.hikari HikariDataSource HikariConfig]))



(defrecord DbComponent []
  component/Lifecycle
  (start [{:keys [:kosmos/requires] :as  component}]
    (log/info "Starting Database component.")
    (try
      (let [ds (connection/->pool HikariDataSource (into {} (apply dissoc component
                                                                   :kosmos/init
                                                                   :kosmos/requires
                                                                   requires)))]
        (when ds (.close (jdbc/get-connection ds)))
        ds)
      (catch Exception e
        (log/error "Database is not running or is not accessible with the current settings: " component)
        (throw e))))

  (stop [component]
    (log/info "Stopping Database component.")
    (when component ( component))
    (if-let [original (:original (meta component))]
      original
      component)))
