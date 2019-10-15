import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def f(v1, v2):
  return v1 / min(v1, v2)


if __name__ == '__main__':
  rat_3 = [ ]
  rat_4 = [ ]
  
  p_3_mip = [ ]
  p_3_ded = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'SR2EDP_4', topology)
      if data != None:
        for r in data['pairResults']:
          rt_mip = float(r['mip_runtime'])
          rt_ded = float(r['dedicated_runtime'])
          rat_3.append(f(float(rt_mip), float(rt_ded)))
          p_3_mip.append(f(rt_mip, rt_ded))
          p_3_ded.append(f(rt_ded, rt_mip))
  plt.axvline(x = 1, color = 'k', linestyle='--')
  x, y = utils.cdf(p_3_mip, 0.1)
  p_3_mip.sort()
  plt.plot(x, y, label="mip")
  x, y = utils.cdf(p_3_ded, 0.1)
  p_3_ded.sort()
  plt.plot(x, y, label="dedicated")
  plt.legend()
  plt.ylim(0, 110)
  plt.xticks([1, 100, 500, 900])
  plt.xlabel("Ratio between algorithm runtime and minimum runtime (k=4)")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/2SREDP_4.eps', format='eps', dpi=600, bbox_inches='tight')  
