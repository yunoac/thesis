import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  bound_by_size = [ ]
  init_by_size = [ ]
  bound_by_size_complete = [ ]
  init_by_size_complete = [ ]

  lb_ratios = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      if not fn.endswith('.json'): continue
      print(fn)
      V, E = utils.get_graph_size(group, fn)

      topology = fn.split('.')[0]
      data = utils.read_data(group, 'minCover', topology)
      assert data != None, topology
      
      lb1 = float(data['lpBound'])
      init = int(data['initialCoverSize'])
      bound_by_size.append((V + E, lb1))
      init_by_size.append((V + E, init))

      data = utils.read_data(group, 'minCover_complete', topology)
      assert data != None, topology

      lb2 = float(data['lpBound'])
      init = int(data['initialCoverSize'])
      bound_by_size_complete.append((V + E, lb2))
      init_by_size_complete.append((V + E, init))
      lb_ratios.append(lb1 / lb2)

  fig = plt.figure()
  ax = fig.add_subplot(111)
  ax.text(0.3, 90, r'original igp is better', fontsize=12)
  ax.text(1.1, 10, r'complete igp is better', fontsize=12)

  plt.axvline(x = 1, color = 'k', linestyle='--')
  x, y = utils.cdf(lb_ratios, 0.01)


  plt.plot(x, y)
 
  plt.savefig('../data/plot/minCycleCover_lbRatio_cdf.eps', format='eps', dpi=600, bbox_inches='tight')  

