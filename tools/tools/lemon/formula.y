%token_prefix TK_

%token_type {Token}
%default_type {Token}

%stack_overflow { printf("%s\n", "parser stack is overflow."); }



%name formulaParser

%extra_context {Parse *pParse}

/* %include { */
/* #include <stdio.h> */

/*  } */


formula ::= nm EQUAL exp.{ printf("formula is ok.\n"); }
exp ::= num op num.
nm ::= STRING.
num(A) ::= DIGITAL.{
    char tmp[10];
    char *endptr;
    memset(tmp, 0, 10);
    strncpy(tmp, A.z, A.n);
    pParse->data[pParse->count++] = strtol(tmp, &endptr, 10);
}
/* num ::= DIGITAL. */
op ::= ADD.
op ::= MINUS.
