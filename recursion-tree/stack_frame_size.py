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

# Створюємо графік
plt.figure(figsize=(10, 6))
plt.plot(df['Xss_MB'], df['Frame_Size'], marker='o', linestyle='-', color='r')
plt.title('Frame Size vs Stack Size (-Xss)')
plt.xlabel('Stack Size (MB)')
plt.ylabel('Frame Size (bytes)')
plt.grid(True)
plt.xticks(df['Xss_MB'], df['Xss_Value'])

# Додаємо анотації для кожної точки
for i, row in df.iterrows():
    plt.annotate(f'{row["Frame_Size"]:.2f}', (row['Xss_MB'], row['Frame_Size']),
                 textcoords="offset points", xytext=(0,10), ha='center')

# Зберігаємо та показуємо
plt.tight_layout()
plt.savefig('frame_size_vs_stack_size.png')
plt.show()