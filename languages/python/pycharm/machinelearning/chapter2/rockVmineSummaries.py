import sys
import numpy as np
import pylab
import scipy.stats as stats
import pandas as pd
from pandas import DataFrame
import matplotlib.pyplot as plot

__author__ = "ice.zhao"

file_name = "sonar.all-data"


def read_data(file_name):
    xList = []

    f = open(file_name)
    lines = f.readlines()

    for line in lines:
        row = line.strip().split(",")
        xList.append(row)

    nrow = len(xList)
    ncol = len(xList[0])

    return xList, nrow, ncol


def chapter2_main():
    xList = []

    f = open(file_name)
    lines = f.readlines()

    for line in lines:
        row = line.strip().split(",")
        xList.append(row)

    nrow = len(xList)
    ncol = len(xList[0])
    sys.stdout.write("Number of Rows of Data = " + str(len(xList)) + "\n")
    print("number of Columns of data = " + str(len(xList[0])))

    types = [0] * 3
    colCounts = []

    for col in range(ncol):
        for row in xList:
            try:
                a = float(row[col])
                if isinstance(a, float):
                    types[0] += 1
            except ValueError:
                if len(row[col]) > 0:
                    types[1] += 1
                else:
                    types[2] += 1
        colCounts.append(types)
        types = [0] * 3

    sys.stdout.write("Col#" + "\t" + "Number" + "\t" + "Strings" + "\t" + "Other\n")

    i_col = 0
    for types in colCounts:
        print(str(i_col) + "\t\t" + str(types[0]) + '\t\t' + str(types[1]) + '\t\t' + str(types[2]))
        i_col += 1


def chapter2_main1():
    data, nrow, ncol = read_data(file_name)
    types = [0] * 3
    col_counts = []

    col = 3
    col_data = []
    for row in data:
        col_data.append(float(row[col]))

    col_array = np.array(col_data)

    col_mean = np.mean(col_array)
    print("Col " + str(col) + " mean = " + str(col_mean))

    col_sd = np.std(col_array)
    print("Col " + str(col) + " standard deviation = " + str(col_sd))

    ntiles = 4
    percentBdry = []
    for i in range(ntiles + 1):
        percentBdry.append(np.percentile(col_array, i * 100 / ntiles))

    print("boundary for 4 equal percentile: " + str(percentBdry))

    ntiles = 10
    percentBdry = []
    for i in range(ntiles + 1):
        percentBdry.append(np.percentile(col_array, i * 100 / ntiles))

    print("boundary for 10 equal percentile: " + str(percentBdry))

    col = 60
    col_data = []
    for row in data:
        col_data.append(row[col])
    unique = set(col_data)
    print("Unique label values :" + str(unique))

    cat_dict = dict(zip(list(unique), range(len(unique))))
    catCount = [0] * 2
    for elt in col_data:
        catCount[cat_dict[elt]] += 1
    print(catCount)

    # test zip object
    # zip_data = zip(list([1, 2, 3]), range(2))
    # for k in zip_data:
    #     print(k)

    # test percentile concept
    # test_val = [0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 4]
    # test_array = np.array(test_val)
    #
    # ntiles = 4
    # percentBrd = []
    # for i in range(ntiles):
    #     percentBrd.append(np.percentile(test_array, (i+1) * 100 / ntiles))
    #
    # print(percentBrd)


def chapter2_qqplotAttribute():
    data, nrow, ncol = read_data(file_name)
    col = 3
    colData = []
    for row in data:
        colData.append(float(row[col]))

    stats.probplot(colData, dist='norm', plot=pylab)
    pylab.show()


def chapter2_pandasReadSummaries():
    rocksVMines = pd.read_csv(file_name, header=None, prefix="V")
    print(rocksVMines.head())
    print(rocksVMines.tail())

    # print summary of data frame
    summary = rocksVMines.describe()
    print(summary)


def chapter2_linePlots():
    rocksVMines = pd.read_csv(file_name, header=None, prefix="V")
    for i in range(208):
        if rocksVMines.iat[i, 60] == "M":
            pcolor = "red"
        else:
            pcolor = "blue"

        dataRow = rocksVMines.iloc[i, 0:60]
        dataRow.plot(color=pcolor)

    plot.xlabel("Attribute Index")
    plot.ylabel("Attribute Values")
    plot.show()


def chapter2_corrPlot():
    rocksVMines = pd.read_csv(file_name, header=None, prefix="V")
    dataRow2 = rocksVMines.iloc[0:208, 1]
    dataRow3 = rocksVMines.iloc[0:208, 2]

    plot.scatter(dataRow2, dataRow3)
    plot.xlabel("2nd Attribute")
    plot.ylabel("3nd Attribute")
    plot.show()

    dataRow21 = rocksVMines.iloc[0:208, 20]
    plot.scatter(dataRow2, dataRow21)
    plot.xlabel("2nd Attribute")
    plot.ylabel("21st Attribute")
    plot.show()


def chapter2_all():
    # chapter2_qqplotAttribute()
    # chapter2_pandasReadSummaries()
    # chapter2_linePlots()
    chapter2_corrPlot()
