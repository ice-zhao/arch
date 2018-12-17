

main: clear_main
	./lemon formula.y
	gcc -o $@ main.c

lemon: lemon.c
	gcc -o $@ $^


clear_main:
	rm -rf main

clear:
	rm -rf lemon
