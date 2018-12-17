#result needs to divide sqrt(2)

p = var('p')
idx = var('idx')
con = var('con')

sp = [ n(sin(2*pi*p/64))*32700 for p in range(0, 64) ]

for i in sp:
   print str(int(i))+","
    # print str(int(i))

#con=1/32
con = 1

tw = [ con*e^((i*2*pi*(63-p))/64) for p in range(0,64) ]
#tw = [ con*e^((i*2*pi*(p))/64) for p in range(1,65) ]
#tw = [ con*e^((i*2*pi*(p-71))/64) for p in range(0,64) ]
#tw = [ (1/(sqrt(2)*32))*(e^((i*2*pi*(p))/64)) for p in range(0,64) ]

# j = 0
# for elm in tw:
#     j=j+1
#     print(j,n(elm))

# j = 0
# for elm in sp:
#     j = j+1
#     print(j, elm)

res = []
for idx in range(0, 64):
    res.append(sp[idx] * n(tw[idx]))
    #print res[idx]
    #print sp[idx]

res_dft = 0
for elm in res:
    res_dft = res_dft + elm























