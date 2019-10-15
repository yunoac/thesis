#!/usr/local/bin/python
from graphviz import Digraph
from graphviz import Source
import os
import argparse
import pydot

def parse_zoo(fn):
  f = open(fn, 'r')
  lines = [line.strip() for line in f.readlines()]
  data = lines[0].split(' ')
  V = int(data[1])
  print(lines[V + 3])
  data = lines[V + 3].split(' ')
  E = int(data[1])
  edges = [ ]
  for i in range(V + 5, V + 5 + E):
    data = lines[i].split(' ')
    orig = int(data[1])
    dest = int(data[2])
    edges.append( (orig, dest) )
  return V, edges

def parse_ntfl(fn):
  f = open(fn, 'r')
  lines = [line.strip() for line in f.readlines()]
  V = set()
  edges = [ ]
  for line in lines:
    data = line.split(' ')
    orig = line[0]
    dest = line[1]
    V.add(orig)
    V.add(dest)
    edges.append((orig, dest))
  return V, edges

parser = argparse.ArgumentParser()
parser.add_argument('-f', help='graph filename')
args = parser.parse_args()

fn = args.f
V, edges = parse_zoo(fn)

graph = pydot.Dot()
graph.set_type('digraph')

for v in range(V):
  graph.add_node(pydot.Node(v))

for u, v in edges:
  graph.add_edge(pydot.Edge(u, v))

graph.write_pdf(fn + '.pdf')
