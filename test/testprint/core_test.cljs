(ns testprint.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]
     #_[testprint.core :refer [multiply]]))

#_#_
(deftest multiply-test
  (is (= (* 1 2) (multiply 1 2))))

(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))
