(ns gsein-war3.http.server
  (:require [org.httpkit.server :refer :all]))

(defn async-handler [ring-request]
  ;; unified API for WebSocket and HTTP long polling/streaming
  (as-channel ring-request
              {:on-open    (fn [channel]
                             (send! channel {:status  200
                                             :headers {"Content-Type" "text/plain"}
                                             :body    "Long polling?!!DD"}))
               :on-receive (fn [channel message] (println message))}))

(run-server #'async-handler {:port 8090})                   ; Ring server
