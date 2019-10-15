import numpy as np
import matplotlib.pyplot as plt

a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

def median(a):
  n = len(a)
  if n % 2 == 1:
    return a[n // 2]
  else:
    return (a[n // 2 - 1] + a[n // 2]) / 2


q1 = np.percentile(a, 25)
q2 = np.percentile(a, 50)
q3 = np.percentile(a, 75)
print(q1, q2, q3)

l = min(a)
h = max(a)
b = [l, q1, q2, q3, h]

q1 = np.percentile(b, 25)
q2 = np.percentile(b, 50)
q3 = np.percentile(b, 75)
print(q1, q2, q3)


fig, ax = plt.subplots()
ax.set_title('Hide Outlier Points')
ax.boxplot(a, showfliers=False)
ax.boxplot(b, showfliers=False)

plt.show()
