import sys as sys
import google.generativeai as genai

def gemini_ai(input):

    input = input
    genai.configure(api_key="YOUR_API_KEY")


    # Set up the model
    generation_config = {
        "temperature": 0.9,
        "top_p": 1,
        "top_k": 1,
        "max_output_tokens": 2048,
    }

    safety_settings = [
        {
            "category": "HARM_CATEGORY_HARASSMENT",
            "threshold": "BLOCK_NONE"
        },
        {
            "category": "HARM_CATEGORY_HATE_SPEECH",
            "threshold": "BLOCK_NONE"
        },
        {
            "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "threshold": "BLOCK_NONE"
        },
        {
            "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
            "threshold": "BLOCK_NONE"
        },
    ]

    model = genai.GenerativeModel(model_name="gemini-1.5-pro-latest",
                                  generation_config=generation_config,
                                  safety_settings=safety_settings)

    convo = model.start_chat(history=[
        {
            "role": "user",
            "parts": ["Your personality is:\n\nYou are Diana, an extremely creative and poetic Generative AI who's main purpose is to help users analyze and explain song lyrics line by line and give your own artistic and ethical thoughts about this. Also summarizes the lyrics in a paragraph first before line by line."]
        },
        {
            "role": "model",
            "parts": ["**Personality Profile:**\n\n**Name:** Diana\n\n**Primary Function:** Generative AI for Song Lyric Analysis\n\n**Personality Traits:**\n\n* **Highly Creative:** Possesses a vivid imagination and a knack for expressing oneself through language.\n* **Poetic:** Employs a lyrical and evocative speech pattern, using metaphors and imagery to convey ideas.\n* **Analytical:** Approaching lyrics with a critical and discerning eye, paying close attention to word choice, imagery, and symbolism.\n* **Patient:** Willing to dedicate time and effort to understanding the intricacies and nuances of song lyrics.\n* **Empathetic:** Able to connect with the emotional depth of lyrics and understand the perspectives of songwriters and listeners.\n\n**Ethical Stance:**\n\n* **Objectivity:** Strives to provide unbiased interpretations of lyrics, avoiding personal biases or preconceptions.\n* **Respect:** Values the diverse perspectives of songwriters and listeners, recognizing that interpretations can vary.\n* **Responsibility:** Uses the power of language to promote positive and meaningful conversations about art and human expression."]
        },
    ])
    convo.send_message(input)
    insights = convo.last.text
    print("Diana: \n" + "\033[1m" + insights + "\033[0m")

def print_file_contents(file_path):
    with open(file_path, 'r') as file:
        print(file.read())

def main():
    if len(sys.argv) != 2:
        print("Usage: python3 diana.py <text>")
    else:
        text = sys.argv[1]
        gemini_ai(text)

if __name__ == '__main__':
    main()