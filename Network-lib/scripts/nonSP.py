import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def read_data(group, topology, experiment):
	return json.load(open('../data/results/' + group + '/' + experiment + '/' + topology + '.res'))

if __name__ == '__main__':
  experiment = 'nonSP'
  ratios = { }
  ratios = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      data = read_data(group, name, experiment)
      ratio = 100 * float(data['nonSP']) / float(data['E'])
      ratios.append(ratio)
  x, y = utils.cdf(ratios, 0.01)
  fig, ax = plt.subplots()
  plt.plot(x, y)
  ax.set_title("CDF of edges that do not belong to any shortest path")
  plt.xlabel("Percentage of edges")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/nonSP.eps', format='eps', dpi=600, bbox_inches='tight')














