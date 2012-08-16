(ns vcf.scratch
  (:use-macros [c2.util :only [p pp]]))

(set! *print-fn* (fn [x] (p x)))


(.getJSON js/$ "big.json"
          (fn [ds]
            (def cf (js/crossfilter ds))
            (let [qual (.dimension cf (fn [d] (.-QUAL d)))]
              (p (.top qual 10)))
            

            ))
