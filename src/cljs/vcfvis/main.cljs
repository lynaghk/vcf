(ns vcfvis.main
  (:use-macros [c2.util :only [pp p]])
  (:require [vcfvis.data :as data]))

(pp (data/load "vendor/bcbio.variation/test/data/freebayes-calls.vcf"))




