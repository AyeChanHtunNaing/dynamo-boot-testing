import random
import uuid
import pandas as pd

# List of Japanese first names in Kanji
japanese_first_names = [
    '陽翔', '悠斗', '颯太', '結城', '隼人', '陽葵', '海斗', '空', '陸', '日向',
    '葵', '桜', '莉奈', '優奈', '美優', '愛莉', '花', '咲', '明', '涼', '蓮', '美桜'
]


n_records = 100000
dummy_data_jp = {
    'employeeId': [str(uuid.uuid4()) for _ in range(n_records)],
    'firstName': [random.choice(japanese_first_names) for _ in range(n_records)],
    'lastName': ['LastName_' + str(i) for i in range(n_records)],
    'email': ['email_' + str(i) + '@test.com' for i in range(n_records)]
}

# Create a DataFrame with the dummy data
dummy_df_jp = pd.DataFrame(dummy_data_jp)

# Save the dummy data to a CSV file
output_file_path_jp_50000 = './testing_1lakh.csv'
dummy_df_jp.to_csv(output_file_path_jp_50000, index=False)

output_file_path_jp_50000
