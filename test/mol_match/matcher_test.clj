(ns mol-match.matcher-test
  (:require [clojure.test :refer [are is deftest testing]]
            [mol-match.matcher :as SUT]))

(deftest matched-pair?-test
  (testing "true-matches"
    (are [t b] (true? (SUT/matched-pair? t b))
      \C \N
      \N \C))

  (testing "false-matches"
    (are [t b] (false? (SUT/matched-pair? t b))
      \C \C
      \N \N
      \x \C
      \C \x
      \x \N
      \N \x
      \x \x
      \p \q)))

(deftest count-matches-helper-test
  (is (= 2 (SUT/count-matches-helper SUT/top-a SUT/bot-a)))
  (is (= 1 (SUT/count-matches-helper SUT/top-b SUT/bot-b)))
  (is (= 1 (SUT/count-matches-helper SUT/top-a SUT/bot-b))))

(deftest count-matches-test
  (is (= 2 (SUT/count-matches "A" "A")))
  (is (= 1 (SUT/count-matches "B" "B")))
  (is (= 1 (SUT/count-matches "A" "B"))))

(def oh \0)
(def one \1)

(deftest make-binary-seq-test
  (are [length digits result] (= result (SUT/make-binary-seq length digits))
    6 5  '(\0 \0 \0 \1 \0 \1)
    8 5 '(\0 \0 \0 \0 \0 \1 \0 \1)
    2 1 '(\0 \1)
    9 8 '(\0 \0 \0 \0 \0 \1 \0 \0 \0)
    3 8 '(\0 \0 \0)
    9 11 '(\0 \0 \0 \0 \0 \1 \0 \1 \1)
    5 0 '(\0 \0 \0 \0 \0)))

(deftest binary->letter-seq-test
  (are [length digits result] (= result (SUT/binary->letter-seq length digits))
    6 5 '(\A \A \A \B \A \B)
    8 5 '(\A \A \A \A \A \B \A \B)
    2 1 '(\A \B)
    9 8 '(\A \A \A \A \A \B \A \A \A)
    3 8 '(\A \A \A)
    9 11 '(\A \A \A \A \A \B \A \B \B)
    5 0 '(\A \A \A \A \A)))
