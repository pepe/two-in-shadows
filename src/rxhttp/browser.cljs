;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) 2017 Andrey Antukh <niwi@niwi.nz>

(ns rxhttp.browser
  "A streams based http client for clojurescript (browser and node)."
  (:require [clojure.string :as str]
            [beicon.core :as rx]
            [goog.events :as events])
  (:import [goog.net ErrorCode EventType]
           [goog.net.XhrIo ResponseType]
           [goog.net XhrIo]
           [goog.Uri QueryData]
           [goog Uri]))

(defn translate-method
  "Translates the keyword method name to internal http method naming."
  [method]
  (case method
    :head    "HEAD"
    :options "OPTIONS"
    :get     "GET"
    :post    "POST"
    :put     "PUT"
    :patch   "PATCH"
    :delete  "DELETE"
    :trace   "TRACE"))

(defn normalize-headers
  "Normalize all headers into a lowe-case keys map."
  [headers]
  (reduce-kv (fn [acc k v]
               (assoc acc (str/lower-case k) v))
             {} (js->clj headers)))

(defn translate-error-code
  [code]
  (condp = code
    ErrorCode.TIMEOUT    :timeout
    ErrorCode.EXCEPTION  :exception
    ErrorCode.HTTP_ERROR :http
    ErrorCode.ABORT      :abort))

(defn translate-response-type
  [type]
  (case type
    :text ResponseType.TEXT
    :blob ResponseType.BLOB
    ResponseType.DEFAULT))

(defn build-uri
  "Build a XhrIo compatible uri string."
  [url qs qp]
  (let [uri (Uri. url)]
    (when qs (.setQuery uri qs))
    (when qp
      (let [dt (.createFromMap QueryData (clj->js  qp))]
        (.setQueryData uri dt)))
    (.toString uri)))

(defn success?
  "Check if the provided response has a SUCCESS status code."
  [{:keys [status]}]
  (<= 200 status 299))

(defn server-error?
  "Check if the provided response has a SERVER ERROR status code."
  [{:keys [status]}]
  (<= 500 status 599))

(defn client-error?
  "Check if the provided response has a CLIENT ERROR status code."
  [{:keys [status]}]
  (<= 400 status 499))

(defn send!
  "Send a http request and return a `Observable` that will emit the
  response when it is returned by the server. This is a lazy
  operation, this means that until you will not subscribe to the
  resulting observable no request will be fired.

  This is an example of a complete request hash-map:

    {:method :put                    ;; (can be :get :post :put :delete :head :options)
     :headers {}                     ;; (a map of key values that will be sent as headers)
     :url \"http://httpbin.org/get\" ;; (a destination url)
     :query-params {:q \"foo\"}      ;; (a hash-map with query params)
     :query-string \"q=bar\"         ;; (a string with query params if you want raw access to it)
     :body \"foobar\"}               ;; (a body if proceed, can be anything that the underlying
                                         platform can accept: FormData, string, Buffer, blob, ...)

  This method accept and additional optional parameter for provide
  some additional options:

    - `:timeout`:       a number of milliseconds after which the client will
                        close the observable with timeout error.
    - `:credentials?`:  specify if allow send cookies when making a request to
                        a different domain (browser only).
    - `:response-type`  specify the type of the body in the response, can be
                        `:text` and `:blob` (in nodejs `:blob` means that the body
                        will be a instance of `Buffer).

  Here an example of using this method:

    (-> (http/send! {:method :get :url \"https://httpbin.org/get\"})
        (rx/subscribe (fn [response]
                        (println \"Response:\" response))))
  "
  ([request] (send! request {}))
  ([{:keys [method url query-string query-params headers body] :as request}
    {:keys [timeout credentials? response-type]
     :or {timeout 0 credentials? false response-type :text}}]
   (let [uri (build-uri url query-string query-params)
         headers (if headers (clj->js headers) #js {})
         method (translate-method method)]
     (rx/create
      (fn [sink]
        (let [xhr (.send XhrIo uri nil method body headers timeout credentials?)]
          (.setResponseType xhr (translate-response-type response-type))
          (events/listen xhr EventType.COMPLETE
                         (fn []
                           (if (or (= (.getLastErrorCode xhr) ErrorCode.HTTP_ERROR)
                                   (.isSuccess xhr))
                             (let [rsp {:status (.getStatus xhr)
                                        :body (.getResponse xhr)
                                        :headers (normalize-headers
                                                  (.getResponseHeaders xhr))}]
                               (sink (rx/end rsp)))
                             (let [type (-> (.getLastErrorCode xhr)
                                            (translate-error-code))
                                   message (.getLastError xhr)]
                               (sink (ex-info message {:type type}))))))
          #(.abort xhr)))))))
