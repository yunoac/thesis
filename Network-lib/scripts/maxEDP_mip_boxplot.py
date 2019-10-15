import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  runtime = [ ]
  labels = [ ]
  for group in ['rf', 'real']:
    for fn in utils.listGroupInstances(group):
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'maxEDPMip', topology)
      assert data != None, topology
      res = data['pairResults']
      times = [ ]
      for r in res:
        times.append(float(r['runtime']) / 1e9)
      runtime.append(times)
      labels.append(topology)
  print(labels)
  # plot
  fig, ax = plt.subplots()
  bp = ax.boxplot(runtime, showfliers=False, patch_artist=True)
  print('plotted')
  plt.xticks(range(1, len(labels) + 1), labels, rotation='vertical')
  for e in utils.bp_elems():
    plt.setp(bp[e], color='#1C2331')
  for patch in bp['boxes']:
    patch.set(facecolor='#3F729B')
  plt.setp(bp['medians'], color='#00C851')
  ax.set_title('Number of segments required for the minimum latency path')
  plt.xlabel("Topologies")
  plt.ylabel("Number of segment in minimum latency path")
  plt.savefig('../data/plot/maxEDP_boxplot.eps', format='eps', dpi=600, bbox_inches='tight')
