marg:
	lein marg
	cp ./docs/uberdoc.html /tmp/uberdoc.html
	git checkout gh-pages
	cp /tmp/uberdoc.html ./docs/uberdoc.html
	git commit -avm "updated docs"
	git push origin gh-pages
	git checkout master
