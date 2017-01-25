(ns functions-as-patterns.seq
  (:require [functions-as-patterns.core :refer :all]))

;;Get shorter
;;;distinct filter remove take-nth for

(view (distinct (sort (concat (hues 5) (hues 5)))))

;;The pattern is more the fn than the filter/remove
(view (filter (fn [x] (= 0 (mod x 3))) (hues 10)))
(view (remove (fn [x] (= 0 (mod x 3))) (hues 10)))

(view (take-nth 3 (hues 10)))
(view (take 5 (for [x (range 5) y (range 5) :while (< y x)] [(int->color x)
                                                             (int->color y)])))

;;Get longer
;;;cons conj concat lazy-cat mapcat cycle interleave interpose

(view (cons   rgb-highlight-color (color-seq 3)))
(view (conj   (color-seq 3)        rgb-highlight-color))
(view (concat (color-seq 3) (color-seq 3 rgb-highlight-color)))
;;fails (view (conj (hues 5) (hues 5)))
;;fails (view (lazy-cat (color-seq 3) (color-seq 3 rgb-highlight-color)))

(view (mapcat (fn [x] x) [(color-seq 3) (color-seq 3 rgb-highlight-color)]))
(view (interpose rgb-highlight-color (color-seq 8)))
(view (interleave (hues 30 2 highlight-color) (color-seq 8)))

;;Tail-items
;;;rest nthrest next fnext nnext drop drop-while take-last for

(view (rest (hues 4)))
(view (nthrest (hues 10) 4))

;;Head-items
;;;take take-while butlast drop-last for

;;Change
;;;conj concat distinct flatten group-by partition partition-all partition-by split-at split-with filter
;;;remove replace shuffle

(view (shuffle (interpose rgb-highlight-color (color-seq 5))))
(view (replace (hues 10) [0 3 4 5]))
(view (partition 2 (hues 10)))

;;Rearrange
;;;reverse sort sort-by compare

;;Process items
;;;map pmap map-indexed mapcat for replace seque
