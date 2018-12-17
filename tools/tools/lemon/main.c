#include <stdio.h>
#include "formula.h"
#include <string.h>
#include <stdlib.h>



struct Parse {
    long data[10];
    int count;
};

struct Token {
    const char *z;     /* Text of the token.  Not NULL-terminated! */
    unsigned int n;    /* Number of characters in this token */
};
typedef struct Token Token;
typedef struct Parse Parse;



int get_token(const char *str, int *token_type);
void formula_add(long *val, Parse *pParse);

#include "formula.c"


long val = 0;
int apple = 3;
int pear = 5;

int main(int argc, char* argv[])
{
    char *expr = "val = 123 + 65";
    int n = 0;
    int token_type = 0;
    int i = 0;
    Parse parse;
    Token stoken;

    void *pEngine = formulaParserAlloc(malloc, &parse);
    if( pEngine == NULL ) {
        printf("alloc engine failure.\n");
        return -1;
    }

    memset(&parse, 0, sizeof(Parse));
    formulaParserInit(pEngine, &parse);

    while( (n = get_token(expr, &token_type)) > 0 ) {
        stoken.z = expr;
        stoken.n = n;
        for( i=  0; i < stoken.n; i++ )
            printf("%c", stoken.z[i]);
        printf("  |%d", token_type);
        printf("\n");

        if( expr[n] != '\0' ) {
            expr += n;
            while( *expr == ' ' ) { expr++; }
        }
        else
            expr = NULL;

        formulaParser(pEngine, token_type, stoken);
    }
    formulaParser(pEngine, 0, stoken);

    /* for(i = 0; i < parse.count; i++) { */
    /*     printf("----%ld\n", parse.data[i]); */
    /* } */

    formula_add(&val, &parse);
    printf("val = %ld\n", val);

    if( pEngine != NULL ) {
        formulaParserFree(pEngine, free);
        pEngine = NULL;
    }

    return 0;
}

int get_token(const char *str, int *token_type)
{
    int pos = 0;
    if( str == NULL || token_type == NULL )
        return -1;

    char *p = NULL;
    int count = 0;
    int i = 0;
    char *space = " ";

    while( str[pos] != '\0' ) {
        p = strstr(str, space);
        if( p != NULL ) {
            count = p - str;
            for( i = 0; i < count; i++ ) {
                if( (str[i] >= 'a' && str[i] <= 'z') )
                    continue;
                else
                    break;
            }
            if( i == count ) {
                *token_type = TK_STRING;
                pos = count;
                break;
            }

            for( i = 0; i < count; i++ ) {
                if( (str[i] >= '0' && str[i] <= '9') )
                    continue;
                else
                    break;
            }
            if( i == count ) {
                *token_type = TK_DIGITAL;
                pos = count;
                break;
            }

            if( str[pos] == '=' ) {
                *token_type = TK_EQUAL;
                pos = count;
                break;
            }
            if( str[pos] == '+' ) {
                *token_type = TK_ADD;
                pos = count;
                break;
            }
            if( str[pos] == '-' ) {
                *token_type = TK_MINUS;
                pos = count;
                break;
            }

        }// p != NULL
        pos++;
        *token_type = TK_DIGITAL;
    }//while

    return pos;
}


void formula_add(long *val, Parse *pParse)
{
    *val = pParse->data[0] + pParse->data[1];
}
