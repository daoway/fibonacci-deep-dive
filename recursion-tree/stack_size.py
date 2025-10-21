import pandas as pd
import matplotlib.pyplot as plt

# Читаємо CSV
df = pd.read_csv('results.csv')

# Конвертуємо Xss_Value в числові значення (k=0.5 MB, m=1 MB)
def xss_to_mb(xss):
    if xss.endswith('k'):
        return float(xss[:-1]) / 1024
    elif xss.endswith('m'):
        return float(xss[:-1])
    return 0

df['Xss_MB'] = df['Xss_Value'].apply(xss_to_mb)

# Сортуємо за Xss_MB
df = df.sort_values('Xss_MB')

# Малюємо графік
plt.figure(figsize=(10, 6))
plt.plot(df['Xss_MB'], df['Max_Depth'], marker='o', linestyle='-', color='b')
plt.title('Max Recursion Depth vs Stack Size (-Xss)')
plt.xlabel('Stack Size (MB)')
plt.ylabel('Max Depth')
plt.grid(True)
plt.xticks(df['Xss_MB'], df['Xss_Value'])  # Показуємо оригінальні значення на осі X
plt.show()