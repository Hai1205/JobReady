/**
 * Test cases for suggestion applier
 * Run this in console to verify parsing works correctly
 */

// Test data based on the response format
const testSuggestions = [
    {
        id: "b3a9f1e7-9909-4f52-9c47-c8cb9f6d435d",
        type: "error",
        section: "summary",
        lineNumber: null,
        message: "Summary chỉ có 1 câu, cần mở rộng hơn.",
        suggestion: "Before: 'Experienced software developer with 5+ years in full-stack development.'\nAfter: 'Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.'",
        applied: false
    },
    {
        id: "cc1f3c73-9c52-4f65-bcbd-67e40d7de8ee",
        type: "warning",
        section: "experience",
        lineNumber: null,
        message: "Experience at TechCorp Inc. thiếu metrics định lượng.",
        suggestion: "Before: 'Lead development of web applications using React, Node.js, and AWS.'\nAfter: 'Led development of web applications using React, Node.js, and AWS, resulting in a 30% increase in user satisfaction and reduced deployment time by 20%.'",
        applied: false
    },
    {
        id: "e8a039c5-482b-414f-af74-3703bb5d1c7f",
        type: "improvement",
        section: "skills",
        lineNumber: null,
        message: "Skills cần phân loại rõ ràng và thêm nhiều kỹ năng liên quan.",
        suggestion: "Phân loại kỹ năng: 'Technical Skills: JavaScript, React, Node.js. Soft Skills: Leadership, Mentoring, Problem-solving.'",
        applied: false
    }
];

// Expected results after parsing
const expectedResults = {
    summary: "Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.",
    experience: "Led development of web applications using React, Node.js, and AWS, resulting in a 30% increase in user satisfaction and reduced deployment time by 20%.",
    skills: ["JavaScript", "React", "Node.js", "Leadership", "Mentoring", "Problem-solving"]
};

console.log("Test Suggestions:", testSuggestions);
console.log("Expected Results:", expectedResults);

// To test in browser console:
// 1. Import the applySuggestionToCV function
// 2. Create a mock CV object
// 3. Apply each suggestion and verify the results

export { testSuggestions, expectedResults };
