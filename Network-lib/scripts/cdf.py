import numpy as np
from pylab import *
import argparse
import os

def cdf(data, step):
  data.sort()
  count = 0
  x = [ ]
  y = [ ]
  n = len(data)
  value = min(data)
  i = 0
  while i < n:
    if data[i] <= value:
      count += 1
      i += 1
    else:
      x.append(value)
      y.append(count / n)
      value += step
  x.append(data[-1])
  y.append(1)
  return x, [100 * z for z in y]

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument('-d', help='the directory name')
  parser.add_argument('-o', help='the output filename')
  parser.add_argument('-i', help='the index of the values (default is 0)')
  parser.add_argument('-l', help='labels')
  parser.add_argument('-t', help='title')
  args = parser.parse_args()
  title = args.t if args.t != None else 'untitled'
  dirname = args.d if args.d != None else '.'
  files = os.listdir(dirname)
  labels = args.l.split(' ') if args.l != None else [ str(i) for i in range(len(files)) ]
  i = 0
  files.sort()
  for fn in files:
    print(fn)
    index = int(args.i) if args.i != None else 0
    print('processing file {0} column index {1}'.format(fn, index))
    f = open(dirname + fn, 'r')
    data = [ float(line.split(' ')[index]) for line in f.readlines() ]
    x, y = cdf(data, 0.01)
    plot(x, y, label=labels[i])
    maxx = max(x)
    plt.axvline(x=maxx, color='#000000', linestyle='--')
    i += 1
  outputfn = args.o if args.o != None else 'cdf'
  plt.xlabel('ratio of pairs for which the edge can be reached')
  plt.ylabel('percentage of edges')
  plt.legend(loc='lower right')
  plt.title(title)
  plt.savefig(outputfn + '.eps', format='eps', dpi=600, bbox_inches='tight')
