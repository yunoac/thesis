import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  runtime1 = [ ]
  runtime2 = [ ]
  skip = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'maxEDPSegmentModel', topology)
      if data == None:
        skip.append(topology) 
        continue      
      res = data['pairResults']
      for r in res:
        runtime2.append(float(r['runtime']) / 1e9)
   
      data = utils.read_data(group, 'maxEDPMip', topology)
      assert data != None, topology
      res = data['pairResults']
      for r in res:
        runtime1.append(float(r['runtime']) / 1e9)  
  x, y = utils.cdf(runtime1, 0.01)
  ax = plt.subplot()
  ax.set_xscale("log")
  plot(x, y, label='Fortz')
  x, y = utils.cdf(runtime2, 0.01)
  plot(x, y, label='Segment')
  plt.axvline(x=max(runtime2), color='k', linestyle='--')
  ax.legend()
  #plt.xticks(list(plt.xticks()[0]) + [max(runtime2)])
  plt.xlabel("Runtime in seconds")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/maxEDPMip_runtime_both.eps', format='eps', dpi=600, bbox_inches='tight')  
  print(skip)
  print(max(runtime2))
