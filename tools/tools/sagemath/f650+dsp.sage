# aux = 50 * 64   #50HZ * 64 points/cycle
# print "aux = %d" % aux
aux = 60 * 64   #60HZ * 64 points/cycle
print "aux = %d" % aux

dsp_clock = 250000000   #250M
pcg_fs = dsp_clock / ( aux * 10 ) + 0.5
print "pcg_fs = %f, 0x%x" % (pcg_fs, pcg_fs)

pcg = pcg_fs * 0.75 + 0.5
print "pcg = %d, 0x%x, %f" % (pcg, pcg, pcg)


