import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def read_data(group, topology):
	return json.load(open('../data/results/' + group + '/minLat/' + topology + '.res'))

if __name__ == '__main__':
  labels = [ ]
  M = { }
  zoo_delta = [ ]
  all_delta = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      if group != 'zoo': labels.append(name)
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
          x = int(res['minLatSeg'])
          if group == 'zoo':
            zoo_delta.append(x)
          else:
            delta_lat.append(x)
          all_delta.append(x)
      if group != 'zoo':
        M[name] = delta_lat
  M['zoo'] = zoo_delta
  M['all'] = all_delta

  split = 6

  data = M['all']
  m = max(data)
  count = [ 0 for _ in range(m + 1) ]
  for i in range(len(data)):
    val = data[i]
    count[val] += 1
  s = sum(count)
  count = [100 * x / s for x in count]
  big = 0
  for i in range(split, m+1):
    big += count[i]
  print('<= 5 for : ' + str(100 - big))
  fig, ax = plt.subplots()
  plt.xticks(range(m+1))
  plt.bar(range(m+1), count, color="#3F729B")
  ax.set_title('Number of segments required for the min lat path (all)')
  plt.xlabel("Segment cost")
  plt.ylabel("Percentage of pairs over all topologies")
  plt.savefig('../data/plot/minLat_seg.eps', format='eps', dpi=600, bbox_inches='tight')


  data = M['OVH-EUR']
  m = max(data)
  count = [ 0 for _ in range(m + 1) ]
  for i in range(len(data)):
    val = data[i]
    count[val] += 1
  s = sum(count)
  count = [100 * x / s for x in count]
  big = 0
  for i in range(split, m+1):
    big += count[i]
  print('> 5 for : ' + str(big))
  fig, ax = plt.subplots()
  plt.xticks(range(m+1))
  plt.bar(range(m+1), count, color="#3F729B")
  ax.set_title('Number of segments required for the min lat path (OVH-EUR)')
  plt.xlabel("Segment cost")
  plt.ylabel("Percentage of pairs for OVH-EUR")
  plt.savefig('../data/plot/minLat_seg_ovh.eps', format='eps', dpi=600, bbox_inches='tight')

