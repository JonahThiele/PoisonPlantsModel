import os
from PIL import Image
from PIL import ImageOps 
import magic
rootdir = './'

for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        path = os.path.join(subdir, file)
        if(magic.from_file(path, mime = True) == "image/jpeg"):
            im = Image.open(path)
            os.remove(path)
            im2 = ImageOps.fit(im, (299, 299), method=0, bleed=0.0, centering=(0.5, 0.5))
            #im.resize((500, 256), Image.Resampling.LANCZOS)
            im2.save(path)
            #os.rename(old_file, new_file)
            #path = os.path.join(path, new_file)
            #im.save(new_file)
