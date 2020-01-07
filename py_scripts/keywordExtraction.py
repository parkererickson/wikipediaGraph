#credits: https://github.com/kavgan/nlp-in-practice/blob/master/tf-idf/Keyword%20Extraction%20with%20TF-IDF%20and%20SKlearn.ipynb
import pandas as pd
import numpy as np
import re
from sklearn.feature_extraction.text import CountVectorizer

textFiles = False

if textFiles == True:
    files = []
    import os
    for file in os.listdir("../questions/question_data/wiki"):
        if file.endswith(".txt"):
            files.append(file)
    titles = []
    texts = []
    for fl in files:
        filename ="../questions/question_data/wiki/"+fl 
        f=open(filename, "r")
        texts.append(f.read())
        f.close()
        title = filename[:-4].replace("../questions/question_data/wiki/", "")
        titles.append(title)
    data = {"title":titles, "text":texts}
    df_idf = pd.DataFrame.from_dict(data)
else:
    df = pd.read_csv("../questions/question_data/questions.csv")
    print(df.head())
    titles = []
    texts = []
    for index, row in df.iterrows():
        titles.append(row["Answer"])
        texts.append(row["Text"])
    data = {"title":titles, "text":texts}
    df_idf = pd.DataFrame.from_dict(data)

def pre_process(text):
    
    # lowercase
    text=text.lower()
    
    #remove tags
    text=re.sub("</?.*?>"," <> ",text)
    
    # remove special characters and digits
    text=re.sub("(\\d|\\W)+"," ",text)
    
    return text

df_idf['text'] = df_idf['text'].apply(lambda x:pre_process(x))

def get_stop_words(stop_file_path):
    """load stop words """
    
    with open(stop_file_path, 'r', encoding="utf-8") as f:
        stopwords = f.readlines()
        stop_set = set(m.strip() for m in stopwords)
        return frozenset(stop_set)

#load a set of stop words
stopwords=get_stop_words("resources/stopwords.txt")

#get the text column 
docs=df_idf['text'].tolist()

#create a vocabulary of words, 
#ignore words that appear in 85% of documents, 
#eliminate stop words

cv=CountVectorizer(max_df=0.85,stop_words=stopwords,max_features=10000)
word_count_vector=cv.fit_transform(docs)
word_count_vector.shape
print(word_count_vector.shape)

print(list(cv.vocabulary_.keys())[:10])

from sklearn.feature_extraction.text import TfidfTransformer

tfidf_transformer=TfidfTransformer(smooth_idf=True,use_idf=True)
tfidf_transformer.fit(word_count_vector)

def sort_coo(coo_matrix):
    tuples = zip(coo_matrix.col, coo_matrix.data)
    return sorted(tuples, key=lambda x: (x[1], x[0]), reverse=True)

def extract_topn_from_vector(feature_names, sorted_items, topn=10):
    """get the feature names and tf-idf score of top n items"""
    
    #use only topn items from vector
    sorted_items = sorted_items[:topn]

    score_vals = []
    feature_vals = []

    for idx, score in sorted_items:
        fname = feature_names[idx]
        
        #keep track of feature name and its corresponding score
        score_vals.append(round(score, 3))
        feature_vals.append(feature_names[idx])

    #create a tuples of feature,score
    #results = zip(feature_vals,score_vals)
    results= {}
    for idx in range(len(feature_vals)):
        results[feature_vals[idx]]=score_vals[idx]
    
    return results

feature_names=cv.get_feature_names()

titles = []
allKeywords = []
keywordweights = []

for index_label, row_series in df_idf.iterrows():
    topic = row_series["title"]
    doc=row_series["text"]

    tf_idf_vector=tfidf_transformer.transform(cv.transform([doc]))

    sorted_items=sort_coo(tf_idf_vector.tocoo())

    keywords=extract_topn_from_vector(feature_names,sorted_items,10)

    for k in keywords:
        titles.append(topic)
        allKeywords.append(k)
        keywordweights.append(keywords[k])

data = {"title":titles, "keywords":allKeywords, "keywordWeights":keywordweights}
df = pd.DataFrame.from_dict(data)    

df.to_csv("../questions/question_data/questionKeywords.csv", sep=",", index=False,  encoding='utf-8')