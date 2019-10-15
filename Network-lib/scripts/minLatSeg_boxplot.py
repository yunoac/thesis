import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def read_data(group, topology):
	return json.load(open('../data/results/' + group + '/minLat/' + topology + '.res'))

if __name__ == '__main__':
  plt.xscale('log')
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
          x = float(res['minLatSeg'])
          if group == 'zoo':
            zoo_delta.append(x)
          else:
            delta_lat.append(x)
          all_delta.append(x)
      if group != 'zoo':
        M[name] = delta_lat
  M['zoo'] = zoo_delta
  M['all'] = all_delta
  print(np.mean(all_delta))
  # sort labels
  labels.sort()
  labels.append('zoo')
  labels.append('all')
  results = [ M[x] for x in labels ]
  labels = [ utils.topology_name(x) for x in labels ]
  # plot
  fig, ax = plt.subplots()
  bp = ax.boxplot(results, showfliers=False, patch_artist=True)
  print('plotted')
  plt.xticks(range(1, len(labels) + 1), labels, rotation='vertical')
  plt.ylim(-1, 20)
  plt.yticks(range(1, 19))
  for e in utils.bp_elems():
    plt.setp(bp[e], color='#1C2331')
  for patch in bp['boxes']:
    patch.set(facecolor='#3F729B')
  plt.setp(bp['medians'], color='#00C851')
  ax.set_title('Number of segments required for the minimum latency path')
  plt.xlabel("Topologies")
  plt.ylabel("Number of segment in minimum latency path")
  plt.savefig('../data/plot/minLat_seg.eps', format='eps', dpi=600, bbox_inches='tight')
