import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def read_data(group, topology, experiment):
	return json.load(open('../data/results/' + group + '/' + experiment + '/' + topology + '.res'))

if __name__ == '__main__':
  experiment = 'reach'
  ratios = { }
  nodeReach = [ ]
  edgeReach = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      data = read_data(group, name, experiment)
      nodeReach.append(int(data['nodeReach']))
      edgeReach.append(int(data['edgeReach']))
  
  cnt = utils.percentages(nodeReach)
  keys = [k for k in cnt]
  keys.sort()
  data = [cnt[k] for k in keys] 
  fig, ax = plt.subplots()
  R = range(keys[0], keys[-1] + 1)
  plt.xticks(R)
  plt.bar(R, data, color="#3F729B")
  #ax.set_title('Number of segments required to reach all routers with a deterministic sr-path from a single vantage point')
  plt.xlabel("kn(G)")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/nodeReach.eps', format='eps', dpi=600, bbox_inches='tight')

  cnt = utils.percentages(edgeReach)
  keys = [k for k in cnt]
  keys.sort()
  data = [cnt[k] for k in keys] 
  print(data[0] + data[1] + data[2])
  fig, ax = plt.subplots()
  R = range(keys[0], keys[-1] + 1)
  plt.xticks(R)
  plt.bar(R, data, color="#3F729B")
  #ax.set_title('Number of segments required to reach all links with a deterministic sr-path from a single vantage point')
  plt.xlabel("ke(G)")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/edgeReach.eps', format='eps', dpi=600, bbox_inches='tight')





