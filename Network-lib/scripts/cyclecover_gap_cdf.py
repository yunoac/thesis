import numpy as np
from pylab import *
import argparse
import os
import json

import utils

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

def read_data(group, topology):
  if os.path.exists('../data/results/' + group + '/minCover/' + topology + '.res'):
	  return json.load(open('../data/results/' + group + '/minCover/' + topology + '.res'))
  return None

if __name__ == '__main__':
  gaps = [ ]
  diffs = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      topology = fn.split('.')[0]
      data = read_data(group, topology)
      if data == None: continue
      lp = math.ceil(float(data['lpBound']))
      cg = float(data['finalCoverSize'])
      gap = 100 * (cg / lp - 1)
      diffs.append(cg - lp)
      gaps.append(gap)
  x, y = cdf(diffs, 0.01)
  plot(x, y)
  plt.show()
