import numpy as np
from pylab import *
import argparse
import os
import json

import utils

if __name__ == '__main__':
  pd = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'maxEDPMip', topology)
      assert data != None, topology
      res = data['pairResults']
      for r in res:
        pd.append(r['nbpFlow'] - r['nbpSR'])
  """
  pd = utils.data_to_bar(pd)
  groups = [ str(i) for i in range(len(pd)) ]
  utils.make_g_barplot([pd], groups, ['kmax'], ['#ED553B'], 'value of kmin and kmax', 'percentage of topologies', 'kmin and kmax over all topologies', '../data/plot/sredp_vs_edp.eps', 5)
  print(pd) 
  """
