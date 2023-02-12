import os
import magic
rootdir = './'

for subdir, dirs, files in os.walk(rootdir):
	for file in files:
		path = os.path.join(subdir, file)
		if(magic.from_file(path, mime = True) == "text/xml"):
			os.remove(path)