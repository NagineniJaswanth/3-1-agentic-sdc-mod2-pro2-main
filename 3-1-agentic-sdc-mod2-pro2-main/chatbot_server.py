from flask import Flask, request, jsonify
from flask_cors import CORS
import json
from datetime import datetime

app = Flask(__name__)
CORS(app)

# In-memory context storage
user_contexts = {}

class ChatbotEngine:
    def __init__(self):
        self.context_memory = {}

    def get_response(self, user_message, user_id):
        # Get or create user context
        if user_id not in user_contexts:
            user_contexts[user_id] = {"conversation_history": [], "last_topic": None}

        context = user_contexts[user_id]
        message = user_message.lower().strip()

        # Add to history
        context["conversation_history"].append({"role": "user", "content": message})

        # Determine response based on intent and context
        response = self.process_intent(message, context)

        # Add response to history
        context["conversation_history"].append({"role": "assistant", "content": response})

        return response

    def process_intent(self, message, context):
        # Intent detection with context awareness
        if "hello" in message or "hi" in message:
            return self.get_greeting_response()
        elif "how are you" in message:
            return "I'm doing great! Thanks for asking."
        elif "weather" in message:
            context["last_topic"] = "weather"
            return self.get_weather_response()
        elif "time" in message:
            context["last_topic"] = "time"
            return self.get_time_response()
        elif "name" in message:
            context["last_topic"] = "name"
            return self.get_name_response()
        elif "help" in message:
            return self.get_help_response()
        elif "bye" in message or "goodbye" in message:
            return self.get_goodbye_response()
        elif "clear" in message or "reset" in message:
            return self.clear_context(context)
        else:
            # Check for follow-up questions
            if context["last_topic"] and self.is_follow_up(message):
                return self.handle_follow_up(message, context)
            return self.get_general_response(message)

    def is_follow_up(self, message):
        indicators = ["what about", "how about", "and", "also", "then", "so", "more"]
        return any(indicator in message for indicator in indicators) or len(message) < 15

    def handle_follow_up(self, message, context):
        last_topic = context["last_topic"]
        if last_topic == "weather":
            return "Regarding weather, you can check local forecasts. Is there a specific city you're interested in?"
        elif last_topic == "time":
            return "For time information, you can check different time zones. Would you like to know the time in a specific country?"
        elif last_topic == "name":
            return "I'm your AI assistant. What would you like to know more about?"
        else:
            return "I can provide more information about " + last_topic + ". What specifically would you like to know?"

    def get_greeting_response(self):
        return "Hello! I'm your AI assistant. I remember our conversation and can handle follow-up questions."

    def get_weather_response(self):
        return "I can help with weather information. Would you like to know about weather in a specific city?"

    def get_time_response(self):
        return f"The current time is {datetime.now().strftime('%H:%M')}. Is there anything else about time you'd like to know?"

    def get_name_response(self):
        return "I'm your AI assistant. You can call me Assistant. What's your name?"

    def get_help_response(self):
        return "I can help with weather, time, general questions, and more. Just ask!"

    def get_goodbye_response(self):
        return "Goodbye! It was nice talking to you."

    def clear_context(self, context):
        context["conversation_history"] = []
        context["last_topic"] = None
        return "Context cleared. How can I help you now?"

    def get_general_response(self, message):
        return f"I understand you're asking about '{message}'. Could you provide more details?"

@app.route('/chat', methods=['POST'])
def chat():
    data = request.json
    user_id = data.get('user_id', 'default')
    message = data.get('message', '')

    engine = ChatbotEngine()
    response = engine.get_response(message, user_id)

    return jsonify({
        'response': response,
        'timestamp': datetime.now().isoformat()
    })

if __name__ == '__main__':
    app.run(debug=True, port=5000)