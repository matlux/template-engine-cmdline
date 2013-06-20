(ns fleettest.core
  (:use fleet))



(defmacro fileet
  "Convinient way to define test templates"
  [args filename]
  `(fleet ~args (slurp (str "./" ~filename ".fleet")) {:escaping :xml}))


(defn write-file
  "Writes a value to a file"
  [value out-file]
  (spit out-file "" :append false)
  (with-open [out-data (clojure.java.io/writer out-file)]
      (.write out-data (str value))))

(defn read-file [in-file]
  (with-open [rdr (clojure.java.io/reader in-file)]
    (reduce conj [] (line-seq rdr))))







(defn -main
  "I don't do a whole lot."
  [x]
  (let [test-posts (read-string (apply str (read-file "./env.json")))]
    (println x test-posts)
   (write-file ((fileet [post title] "./test.txt") (first test-posts) "Post Template") "./test.txt")))
