/**
 * Debug utility to test suggestion parsing in browser console
 * 
 * Usage:
 * 1. Open browser console
 * 2. Run: testParseSuggestion()
 */

// Test the parseSuggestionParts function
export const testParseSuggestionParts = (suggestionText: string) => {
    console.group('🧪 Testing Suggestion Parts Parser');
    console.log('Original suggestion:', suggestionText);

    // Simulate the parsing logic
    const lines = suggestionText.split('\n');
    let beforeContent = null;
    let afterContent = null;

    // Find Before line
    const beforeLineIndex = lines.findIndex(line => line.trim().startsWith('Before:'));
    if (beforeLineIndex !== -1) {
        const beforeLine = lines[beforeLineIndex];
        beforeContent = beforeLine
            .replace(/^Before:\s*/i, '')
            .trim()
            .replace(/^['"]|['"]$/g, ''); // Remove quotes
    }

    // Find After line
    const afterLineIndex = lines.findIndex(line => line.trim().startsWith('After:'));
    if (afterLineIndex !== -1) {
        const afterLine = lines[afterLineIndex];
        afterContent = afterLine
            .replace(/^After:\s*/i, '')
            .trim()
            .replace(/^['"]|['"]$/g, ''); // Remove quotes
    }

    // If no Before/After format, treat as skills suggestion
    if (!beforeContent && !afterContent) {
        afterContent = suggestionText;
    }

    console.log('✅ Parsed Before:', beforeContent);
    console.log('✅ Parsed After:', afterContent);
    console.groupEnd();

    return { before: beforeContent, after: afterContent };
};

// Test with example suggestions
export const runSuggestionPartsTests = () => {
    console.clear();
    console.log('🚀 Running Suggestion Parts Parser Tests\n');

    const testCases = [
        {
            name: 'Summary Suggestion',
            text: "Before: 'Experienced software developer with 5+ years in full-stack development.'\nAfter: 'Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.'"
        },
        {
            name: 'Experience Suggestion',
            text: "Before: 'Lead development of web applications using React, Node.js, and AWS.'\nAfter: 'Led development of web applications using React, Node.js, and AWS, resulting in a 30% increase in user satisfaction and reduced deployment time by 20%.'"
        },
        {
            name: 'Skills Suggestion',
            text: "Phân loại kỹ năng: 'Technical Skills: JavaScript, React, Node.js. Soft Skills: Leadership, Mentoring, Problem-solving.'"
        }
    ];

    testCases.forEach((testCase, index) => {
        console.log(`\n📋 Test ${index + 1}: ${testCase.name}`);
        console.log('─'.repeat(50));
        testParseSuggestionParts(testCase.text);
    });

    console.log('\n✨ All tests completed!\n');
};

// Add to window for easy access in console
if (typeof window !== 'undefined') {
    (window as any).testParseSuggestionParts = testParseSuggestionParts;
    (window as any).runSuggestionPartsTests = runSuggestionPartsTests;
    console.log('💡 Suggestion parts parser utilities loaded! Run runSuggestionPartsTests() in console to test.');
}
