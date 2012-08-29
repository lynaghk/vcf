(ns vcf.scratch
  (:use-macros [c2.util :only [p pp]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [c2.util :only [clj->js]]
        [c2.core :only [unify]]
        [c2.maths :only [extent]])
  (:require [c2.dom :as dom]
            [c2.scale :as scale]
            [clojure.string :as str]
            [singult.core :as singult]))

(set! *print-fn* (fn [x] (p x)))
