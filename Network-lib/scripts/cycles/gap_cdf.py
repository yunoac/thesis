
from pylab import *
import argparse
import os
import json
import utils


def read_data(group, topology):
  if os.path.exists('../../data/results/' + group + '/minCover/' + topology + '.res'):
	  return json.load(open('../../data/results/' + group + '/minCover/' + topology + '.res'))
  return None


for group in os.listdir('../../data/topologies'):
  for topology in os.listdir('../../data/topologies/' + group):
    data = read_data(group, topology)
    if data != None:
      print(topology)
