package com.resumeanalyzer.nlp.processors;

import com.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SkillsProcessor {

    // Enhanced skills categorization
    private static final Map<String, List<String>> SKILL_CATEGORIES = Map.of(
            "Programming Languages", Arrays.asList(
                    "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "C", "Go", "Rust",
                    "Kotlin", "Swift", "PHP", "Ruby", "Scala", "R", "MATLAB", "Perl", "Dart", "Lua"),
            "Web Technologies", Arrays.asList(
                    "HTML", "CSS", "React", "Angular", "Vue", "Node.js", "Express", "Bootstrap",
                    "jQuery", "SASS", "LESS", "Webpack", "Vite", "Next.js", "Nuxt.js", "Svelte"),
            "Frameworks", Arrays.asList(
                    "Spring", "Spring Boot", "Django", "Flask", "FastAPI", "Rails", "Laravel",
                    "ASP.NET", "Hibernate", "JPA", "Struts", ".NET Core", "Entity Framework"),
            "Databases", Arrays.asList(
                    "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "SQL Server",
                    "SQLite", "Cassandra", "ElasticSearch", "DynamoDB", "Firebase", "Neo4j"),
            "Cloud & DevOps", Arrays.asList(
                    "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Jenkins", "GitLab CI",
                    "GitHub Actions", "Terraform", "Ansible", "Chef", "Puppet", "CircleCI"),
            "Tools & Others", Arrays.asList(
                    "Git", "Maven", "Gradle", "npm", "JUnit", "Selenium", "REST", "GraphQL",
                    "Microservices", "Agile", "Scrum", "JIRA", "Confluence", "Postman", "Swagger"),
            "Data Science & AI", Arrays.asList(
                    "Machine Learning", "AI", "Data Science", "TensorFlow", "PyTorch", "Scikit-learn",
                    "Pandas", "NumPy", "Jupyter", "Apache Spark", "Hadoop", "Keras", "OpenCV"),
            "Mobile Development", Arrays.asList(
                    "Android", "iOS", "React Native", "Flutter", "Xamarin", "Ionic", "Cordova",
                    "Swift", "Objective-C", "Kotlin"));

    public void extractSkills(Resume resume, String text) {
        Map<String, Set<String>> categorizedSkills = new HashMap<>();

        // Initialize categories
        for (String category : SKILL_CATEGORIES.keySet()) {
            categorizedSkills.put(category, new HashSet<>());
        }

        String lowerText = text.toLowerCase();

        // Extract skills by category with context awareness
        for (Map.Entry<String, List<String>> category : SKILL_CATEGORIES.entrySet()) {
            String categoryName = category.getKey();
            List<String> skills = category.getValue();

            for (String skill : skills) {
                if (isSkillPresentInContext(lowerText, skill.toLowerCase())) {
                    categorizedSkills.get(categoryName).add(skill);
                }
            }
        }

        // Pattern-based extraction is more reliable and faster than NLP for skills
        // Skills are usually well-defined technical terms that don't need complex NLP

        // Convert to JSON format
        StringBuilder skillsJson = new StringBuilder("[");
        boolean first = true;

        for (Map.Entry<String, Set<String>> category : categorizedSkills.entrySet()) {
            String categoryName = category.getKey();
            Set<String> skills = category.getValue();

            for (String skill : skills) {
                if (!first) {
                    skillsJson.append(",");
                }
                double confidence = calculateSkillConfidence(text, skill);
                skillsJson.append(String.format(
                        "{\"name\":\"%s\",\"category\":\"%s\",\"confidence\":%.2f}",
                        skill, categoryName, confidence));
                first = false;
            }
        }
        skillsJson.append("]");
        resume.setSkillsJson(skillsJson.toString());
    }

    private boolean isSkillPresentInContext(String text, String skill) {
        // Look for skill with word boundaries and context
        Pattern skillPattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
        return skillPattern.matcher(text).find();
    }

    private double calculateSkillConfidence(String text, String skill) {
        String lowerText = text.toLowerCase();
        String lowerSkill = skill.toLowerCase();

        double confidence = 0.6; // Base confidence

        // Increase confidence based on context
        String[] positiveContexts = {
                "experience", "proficient", "expert", "skilled", "years",
                "developed", "worked", "using", "with", "in", "knowledge",
                "familiar", "programming", "development", "project"
        };

        for (String context : positiveContexts) {
            if (lowerText.contains(context + " " + lowerSkill) ||
                    lowerText.contains(lowerSkill + " " + context)) {
                confidence += 0.1;
            }
        }

        // Check for skill variations or versions
        if (lowerText.contains(lowerSkill + ".js") ||
                lowerText.contains(lowerSkill + " framework") ||
                lowerText.contains(lowerSkill + " development") ||
                lowerText.contains(lowerSkill + " programming")) {
            confidence += 0.1;
        }

        // Check for proficiency levels
        if (lowerText.contains("expert " + lowerSkill) ||
                lowerText.contains("advanced " + lowerSkill)) {
            confidence += 0.2;
        }

        return Math.min(confidence, 1.0);
    }

    public int countSkillsByCategory(Resume resume, String category) {
        String skillsJson = resume.getSkillsJson();
        if (skillsJson == null || skillsJson.equals("[]")) {
            return 0;
        }

        return (int) skillsJson.chars().filter(ch -> ch == '{').count();
    }

    public List<String> extractSoftSkills(String text) {
        List<String> softSkills = Arrays.asList(
                "Leadership", "Communication", "Problem Solving", "Teamwork", "Creativity",
                "Critical Thinking", "Time Management", "Adaptability", "Work Ethic",
                "Interpersonal Skills", "Project Management", "Analytical Skills");

        List<String> foundSoftSkills = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (String skill : softSkills) {
            if (isSkillPresentInContext(lowerText, skill.toLowerCase())) {
                foundSoftSkills.add(skill);
            }
        }

        return foundSoftSkills;
    }
}
