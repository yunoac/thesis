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
	return json.load(open('../data/results/' + group + '/minLat/' + topology + '.res'))

def latency_gain(group, topology):
  data = read_data(group, topology)
  for res in data:
    if not res['pathExists']: continue

def box_plot(data):
  data.sort()
  n = len(data)
  q1 = int(n / 4)
  q3 = int(3 * n / 4)
  median = int(n / 2)
  

if __name__ == '__main__':
  group = 'real'
  max_diff = 0
  max_res = None
  for fn in utils.listGroupInstances(group):
    name = fn.split('.')[0]
    data = read_data(group, name)
    errors = None
    delta_lat = [ ]
    for res in data:
      if res['pathExists']:
        # check for errors
        if len(res['errors']) > 0:
          has_errors = True
          errors = res['errors']
          print(name, res['orig'], res['dest'])
          print(errors)
          exit(0)
        # no errors, gather data
        diff = float(res['nomLat']) - float(res['minLat'])
        if diff > max_diff:
          max_diff = diff
          max_res = (res, name)
  print(max_diff)
  print(max_res)
