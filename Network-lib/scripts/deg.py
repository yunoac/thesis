import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def get_size(V):
  if V <= 20:
    return 'small'
  elif V <= 50:
    return 'medium'
  elif V <= 100:
    return 'large'
  else:
    return 'huge'
  

if __name__ == '__main__':
  V = [ ]
  E = [ ]
  G = [ ]
  nonSP = 0
  ECMP = 0
  sp_count = [ ]
  min_cut = [ ]
  md = 0
  deg = { 'small': [], 'medium': [], 'large': [], 'huge': [] }
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      data = utils.read_data(group, 'Analysis', name)
      V.append(int(data['V']))
      E.append(int(data['E']))
      G.append(int(data['G']))
      size = get_size(int(data['V']))
      for c in data['degrees']:
        md = max(md, int(c))
        deg[size].append(int(c))
  for k in deg:
    print(k, deg[k])
  # plot
  labels = ['small', 'medium', 'large', 'huge' ]
  fig, ax = plt.subplots()
  bp = ax.boxplot([ deg[x] for x in labels ], patch_artist=True)
  print('plotted')
  plt.xticks(range(1, len(labels) + 1), labels, rotation='vertical')
  plt.ylim(0, md + 1)
  plt.yticks([1, 3, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65])
  for e in utils.bp_elems():
    plt.setp(bp[e], color='#1C2331')
  for patch in bp['boxes']:
    patch.set(facecolor='#3F729B')
  plt.setp(bp['medians'], color='#00C851')
  #ax.set_title('Latency gain with SR')
  plt.xlabel("Topologies category")
  plt.ylabel("Node degree")
  plt.savefig('../data/plot/deg_boxplot.eps', format='eps', dpi=600, bbox_inches='tight')
