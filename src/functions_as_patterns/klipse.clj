(ns functions-as-patterns.klipse)

(def canvas-id (atom "canvas-1"))
(def all-hues ["#EE5C96" "#EE5F5C" "#EE9C5C" "#EED85C" "#C7EE5C" "#8AEE5C"
               "#5CEE6B" "#5CEEA8" "#5CEEE5" "#5CBAEE" "#5C7EEE" "#775CEE" "#B45CEE"
               "#EE5CEB" "#EE5CAE" "#EE5C71" "#EE835C" "#EEC05C" "#DFEE5C" "#A2EE5C"
               "#65EE5C" "#5CEE8F" "#5CEECC" "#5CD3EE" "#5C96EE" "#5F5CEE" "#9C5CEE"
               "#D95CEE" "#EE5CC7" "#EE5C8A" "#EE6B5C" "#EEA85C" "#EEE55C" "#BAEE5C"])

(defn color-map [args]
  (let [all-args (flatten (remove (fn [a] (sequential? a)) args))
        colors (reduce (fn [acc [idx v]]
                         (assoc acc v (get acc v (nth all-hues idx))))
                       {}
                       (map vector (range) all-args))]
    colors))

(def square-size 50)
(def indent (/ square-size 2))
(def stroke-size 4)

(defn view [row]
    (let [canvas (js/document.getElementById @canvas-id)
          ctx (.getContext canvas "2d")
          canvas-width (.-width canvas)
          canvas-height (.-height canvas)
          height 2]
      (.clearRect ctx 0 0 canvas-width canvas-height)
      (when (and (sequential? row) (seq row))
      (let [width (count row)
            color-lookup (color-map (vec row))
            cell-width square-size
            cell-height square-size]
        (set! (.. canvas -width) (* square-size (inc (count row))))
        (set! (.. canvas -style -width) (* square-size (inc (count row))))

        (.clearRect ctx 0 0 canvas-width canvas-height)
        (loop [x 0]
          (let [active-color (get color-lookup (nth row x) "#CCC")]
            (set! (.-fillStyle ctx) active-color)
            (set! (.-lineWidth ctx) stroke-size)
            (set! (.-strokeStyle ctx) "black")
            (doto ctx
              (.beginPath)
              (.rect (+ indent (* x cell-width)) (/ cell-height 2) cell-width cell-height)
              (.fill)
              (.strokeRect (+ indent (* x cell-width)) (/ cell-height 2) cell-width cell-height))
            (when (< (inc x) width) (recur (inc x)))
            )))))
  row)
