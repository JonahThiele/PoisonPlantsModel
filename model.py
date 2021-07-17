# TensorFlow and tf.keras
import tensorflow as tf

# Helper libraries
import numpy as np
import matplotlib.pyplot as plt


plant_dataset = tf.keras.datasets.plant 

(train_images, train_labels), (test_images, test_labels) = plant_dataset.load_data()

class_names = [
    'poison ivy','poison oak','poison sumac','wild parsnip',
    'giant hogweed','stinging nettle','manchineel','elderberry',
    'castor bean','daffodil','hemlock','iris','jack-in-the pulpit',
    'wild Poinsettia','pokeweed', 'rosary pea','white snakeroot',
    "angel's trumpet",'deadly nightshade','jimson weed','larkspur',
    'corn cockle',"white baneberry(doll's eye)","fox glove",
    "monkshood","mountain laurel","oleander","white hellebore","death camas"
]

train_images = train_images / 255.0

test_images = test_images / 255.0

model = tf.keras.Sequential([
    tf.keras.layers.Flatten(input_shape=(28, 28)),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dense(10)
])

model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

model.fit(train_images, train_labels, epochs=10)

test_loss, test_acc = model.evaluate(test_images,  test_labels, verbose=2)

print('\nTest accuracy:', test_acc)