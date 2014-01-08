(ns fleettest.core
  (:require [clojure.data.json :as json]
  (:use fleet))



(defmacro fileet
  "Convinient way to define test templates"
  [args filename]
  `(fleet ~args (slurp (str "./" ~filename ".fleet")) {:escaping :xml}))

(defn download-binary [to from]
  (with-open [in  (io/input-stream (io/as-url from))]
    (io/copy in (File. to))))

(defn search-replace [sub-map file] (let [k (keys sub-map)
	   replace-one (fn [[k v] line] (clojure.string/replace line (re-pattern k) v))
	   replace-all (fn [sub-map line] (str (reduce #(replace-one %2 %1) line sub-map) "\n"))]
	(apply str (map #(replace-all sub-map %) (read-file file)))))

(defn write-file
  "Writes a value to a file"
  [value out-file]
  (spit out-file "" :append false)
  (with-open [out-data (clojure.java.io/writer out-file)]
      (.write out-data (str value))))

(defn read-file [in-file]
  (with-open [rdr (clojure.java.io/reader in-file)]
    (reduce conj [] (line-seq rdr))))


(defn instantiate-template-from-clojure-file
  "instantiate a Fleet template."
  [^String templatefile ^String instancefile ^String envfile ^String env]
  (let [sub-map (read-string (apply str (read-file envfile)))]
    (println sub-map)
    (write-file ((fleet [? env] (slurp (str templatefile)) {:escaping :xml}) sub-map env) instancefile)))

(defmacro --> [m firstkey & keys]
  (let [a (map #(list 'get %) keys)]
    `(-> (~m ~firstkey ) ~@a)))


(defn load-json [^String envfile ^java.util.Map sub-map]
  (merge (json/read-str  (apply str (read-file envfile))) sub-map))

(defn instantiate-template-from-file
  "instantiate a Fleet template."
  [^String templatefile ^String instancefile ^String envfile ^java.util.Map sub-map2]
  (let [sub-map (merge (json/read-str  (apply str (read-file envfile))) sub-map2)]  ;; :key-fn keyword
    (println sub-map )
    (write-file ((fleet [?] (slurp (str templatefile)) {:escaping :xml}) sub-map) instancefile)
    sub-map
    ))

(defn instantiate-template
  "instantiate a Fleet template."
  [^String templatefile ^String instancefile ^java.util.Map sub-map]
    (println sub-map)
    (write-file ((fleet [?] (slurp (str templatefile)) {:escaping :xml}) (into {} sub-map)) instancefile))

;; example config file
;; {
;; :groovyhome "/opt/groovy-2.1.7"
;; :javahome "/opt/java/jdk1.7.0_09"
;; }

;; (require '[clojure.data.json :as json])
;;(defn retrieve-jira-msg [jira]
;;    (let [body (json/read-str (:body (retrieve-jira-memoized jira)))]
;;		(-> body (get "fields") (get "summary"))))


(defn -main
  "I don't do a whole lot."
  [templatefile instancefile envfile env]
  (let [test-posts (read-string (apply str (read-file envfile)))]
    (println test-posts)
    (write-file ((fileet [node title] templatefile) test-posts env) instancefile)))
