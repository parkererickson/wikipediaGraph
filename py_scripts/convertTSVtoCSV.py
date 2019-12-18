import pandas as pd 
tsv_file='links.tsv'
csv_table=pd.read_table(tsv_file,sep='\t')
csv_table.to_csv('processedLinks.csv',index=False)