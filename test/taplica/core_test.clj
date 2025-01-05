(ns taplica.core-test
  "During development you may have to restart the REPL between runs, since old tap function
  could still be registered."
  (:require [clojure.test :refer :all]
            [taplica.core :as t]))

(set! *warn-on-reflection* true)

(defn sleep
  "Wait a while for the `tap>` to go through."
  []
  (Thread/sleep 100))

(use-fixtures :once (fn [f]
                      (t/reset)
                      (f)))

(deftest taplica-test
  (testing "Initially, nothing tapped"
    (is (empty? (t/values))))

  (testing "tap>>"
    (is (= "one" (t/tap>> "one")))
    (is (= "two" (t/tap>> "two")))
    (is (= "three" (t/tap>> :foo :bar "three")))
    (is (= "four" (t/tap>> [:foo :bar] "four")))
    (sleep)
    (is (= {[]          ["one" "two"]
            [:foo :bar] ["three" "four"]}
           (t/values))))

  (testing "value"
    (is (= ["one" "two"] (t/value)))
    (is (nil? (t/value :nope)))
    (is (nil? (t/value :foo)))
    (is (= ["three" "four"] (t/value :foo :bar)))
    (is (= ["three" "four"] (t/value [:foo :bar]))))

  (testing "fvalue"
    (is (= "one" (t/fvalue)))
    (is (nil? (t/fvalue :nope)))
    (is (nil? (t/fvalue :foo)))
    (is (= "three" (t/fvalue :foo :bar)))
    (is (= "three" (t/fvalue [:foo :bar]))))

  (testing "lvalue"
    (is (= "two" (t/lvalue)))
    (is (nil? (t/lvalue :nope)))
    (is (nil? (t/lvalue :foo)))
    (is (= "four" (t/lvalue :foo :bar)))
    (is (= "four" (t/lvalue [:foo :bar]))))

  (testing "tap!"
    (is (= "one" (t/tap! "one")))
    (is (= "two" (t/tap! "two")))
    (is (= "three" (t/tap! :foo :bar "three")))
    (is (= "four" (t/tap! [:foo :bar] "four")))
    (sleep)
    (is (= {[]          ["two"]
            [:foo :bar] ["four"]}
           (t/values))))

  (testing "pause"
    (t/pause)
    (is (= "five" (t/tap! :new "five")))
    (is (= "six" (t/tap>> :new "six")))
    (sleep)
    (is (= {[]          ["two"]
            [:foo :bar] ["four"]}
           (t/values))))

  (testing "resume after pause"
    (t/resume)
    (is (= "five" (t/tap! :new "five")))
    (is (= "six" (t/tap>> :new "six")))
    (sleep)
    (is (= {[]          ["two"]
            [:new]      ["five" "six"]
            [:foo :bar] ["four"]}
           (t/values))))

  (testing "reset"
    (t/reset)
    (is (empty? (t/values))))

  (testing "stop"
    (t/stop)
    (is (thrown-with-msg? Exception #"Taplica stopped\."
                          (t/tap! :new "five")))
    (is (thrown-with-msg? Exception #"Taplica stopped\."
                          (t/tap>> :new "six")))
    (sleep)
    (is (empty? (t/values))))

  (testing "resume after stop"
    (t/resume)
    (is (= "five" (t/tap! :new "five")))
    (is (= "six" (t/tap>> :new "six")))
    (sleep)
    (is (= {[:new] ["five" "six"]}
           (t/values))))

  (testing "regular tap> does not update the atom"
    (tap> "what?")
    (sleep)
    (is (= {[:new] ["five" "six"]}
           (t/values)))))
