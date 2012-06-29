(ns vcfvis.core
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [computed-observable]])
  )


(def !available-filenames
  "User's files available on the server.
   May want to refactor this design if users have more than a few dozen files."
  (atom []))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))

#_(def !available-metics
  (computed-observable 
   ))

(def !metric
  "The metric to be displayed on the histogram."
  (atom {}))






