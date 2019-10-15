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
	return json.load(open('../data/results/' + group + '/ecmp/' + topology + '.res'))

if __name__ == '__main__':
  labels = [ ]
  M = { }
  zoo_delta = [ ]
  all_delta = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      if group != 'rf' and group != 'real': continue
      name = fn.split('.')[0]
      if group != 'zoo': labels.append(name)
      data = read_data(group, name)
      errors = None
      delta_lat = [ ]
      for res in data:
        # no errors, gather data
        if(int(res['pathCount']) > 0):
          x = float(res['maxLat']) - float(res['minLat'])
          if group == 'zoo':
            zoo_delta.append(x)
          else:
            delta_lat.append(x)
          all_delta.append(x)
        if group != 'zoo':
          M[name] = delta_lat
  M['zoo'] = zoo_delta
  M['all'] = all_delta
  data = M['all']
  x, y = cdf(data, 0.1)
  print(max(data))
  plot(x, y)
  plt.show() 
"""
  count = [ 0 for _ in range(m + 1) ]
  for i in range(len(data)):
    val = data[i]
    count[val] += 1
  count[0] = 0
  print('max: ', max(count))
  s = sum(count)
  count = [100 * x / s for x in count]
  final = [ count[i] for i in range(1, 11) ]
  final.append(0)
  for i in range(11, len(count)):
    final[10] += count[i]
  print(sum(final))
  fig, ax = plt.subplots()
  #plt.xticks(range(m+1))
  labels = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, '> 10' ]
  plt.xticks(range(len(labels)), labels, rotation='vertical')
  plt.bar(range(len(final)), final, color="#3F729B")
  ax.set_title("Distribution of the number of ECMP over all topologies")
  plt.xlabel("Number of ECMP")
  plt.ylabel("Percentage of pairs")
  plt.savefig('../data/plot/ecmpPathCount.eps', format='eps', dpi=600, bbox_inches='tight')
 """
