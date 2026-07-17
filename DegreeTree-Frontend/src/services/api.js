// API configuration
const API_BASE_URL = 'http://localhost:8080'; // Update this to match your backend URL

// Utility function for making API requests
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
};

// API functions
export const courseApi = {
  // Fetch all courses
  getAllCourses: async () => {
    try {
      const courses = await apiRequest('/courses');
      // Transform courses to ensure consistent format
      return courses.map(course => ({
        courseCode: course.courseCode,
        courseName: course.courseName,
        courseDescription: course.courseDescription || '',
        credits: course.credits || 0,
        prerequisiteLogic: course.prerequisiteLogic
      }));
    } catch (error) {
      console.error('Error fetching courses:', error);
      // Return empty array on error to prevent app crash
      return [];
    }
  },

  // Get a specific course by code
  getCourseByCode: async (courseCode) => {
    try {
      return await apiRequest(`/courses/code/${courseCode}`);
    } catch (error) {
      console.error(`Error fetching course ${courseCode}:`, error);
      return null;
    }
  },

  // Get prerequisites for a specific course
  getCoursePrerequisites: async (courseCode) => {
    try {
      return await apiRequest(`/courses/code/${courseCode}/prerequisites`);
    } catch (error) {
      console.error(`Error fetching prerequisites for ${courseCode}:`, error);
      return null;
    }
  },

  // Get prerequisite tree for a specific course
  getCoursePrerequisiteTree: async (courseCode) => {
    try {
      return await apiRequest(`/courses/code/${courseCode}/prerequisite-tree`);
    } catch (error) {
      console.error(`Error fetching prerequisite tree for ${courseCode}:`, error);
      return null;
    }
  },

  // Check which courses are eligible given a set of completed courses.
  // Prerequisite evaluation runs on the backend.
  checkEligibility: async (completedCourses) => {
    try {
      const eligible = await apiRequest('/courses/eligibility', {
        method: 'POST',
        body: JSON.stringify({ completed: completedCourses || [] }),
      });
      return eligible.map(course => ({
        courseCode: course.courseCode,
        courseName: course.courseName,
        courseDescription: course.courseDescription || '',
        credits: course.credits || 0,
      }));
    } catch (error) {
      console.error('Error checking eligibility:', error);
      return [];
    }
  },

  // Get prerequisite relationships for graph visualization
  getPrerequisiteRelationships: async () => {
    try {
      const allCourses = await courseApi.getAllCourses();
      const relationships = {};

      // Get prerequisites for each course
      for (const course of allCourses) {
        try {
          const prereqData = await courseApi.getCoursePrerequisites(course.courseCode);
          const prerequisites = [];

          if (prereqData && prereqData.prerequisites) {
            prereqData.prerequisites.forEach(prereq => {
              prerequisites.push(prereq.courseCode);
            });
          }

          relationships[course.courseCode] = prerequisites;
        } catch (error) {
          console.warn(`Could not get prerequisites for ${course.courseCode}:`, error);
          relationships[course.courseCode] = [];
        }
      }

      return relationships;
    } catch (error) {
      console.error('Error fetching prerequisite relationships:', error);
      return {};
    }
  }
};
